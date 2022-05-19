package com.team7.project.user.service.mypageService;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.team7.project.interview.service.InterviewGeneralService;
import com.team7.project.user.dto.UserInfoResponseDto;
import com.team7.project.user.dto.UserRequestDto;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMypageService {

    private final UserRepository userRepository;
    private final InterviewGeneralService interviewGeneralService;

    @Value("${cloud.aws.credentials.access-key-upload}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key-upload}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    private String basicProfile = "profileImg/100.jpeg";

    boolean isStringEmpty(String nickname) {
        return nickname == null || nickname.trim().isEmpty();
    }

    @Transactional
    public UserInfoResponseDto save(UserRequestDto requestDto, User user) throws IOException {
        String profileImageUrl = null;

        userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("없는 사용자입니다.")
        );

        //닉네임을 입력 안했으면, 랜덤 닉네임 저장
        if(isStringEmpty(requestDto.getNickname())){
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            String randomGenName = "윌비@" + number;
            requestDto.setNickname(randomGenName);
        }

        //파일첨부 했으면 크롭 후, 이미지 로컬 및 S3에 저장하기(첨부 안했으면 null->프론트에서 default이미지)
        if (requestDto.getProfileImage() != null) {
            //이미지 파일 여부
            isImageFile(requestDto.getProfileImage());

            //image 파일 받기(for 크롭)
            File getFile = convert(requestDto.getProfileImage());
            BufferedImage originalImage = ImageIO.read(getFile);
            log.info("before crop x : {} ", originalImage.getWidth());
            log.info("before crop y : {} ", originalImage.getHeight());

            int dw = 200, dh = 200;
            int originalImageWidth = originalImage.getWidth();
            int originalImageHeight = originalImage.getHeight();
            int newImageWidth = originalImageWidth ;
            int newImageHeight = originalImageWidth;

            if (newImageHeight>originalImageHeight){
                newImageWidth= (originalImageHeight *dw) /dh;
                newImageHeight = originalImageHeight;
            }
            //이미지 크롭
            BufferedImage cropImg = Scalr.crop(originalImage,
                    (originalImageWidth-newImageWidth)/2,
                    (originalImageHeight-newImageHeight)/2,
                    newImageWidth, newImageHeight);
            //이미지 리사이징
            BufferedImage destImg = Scalr.resize(cropImg, dw, dh);

            log.info("after crop & resizing x : {} ", destImg.getWidth());
            log.info("after crop & resizing y : {} ", destImg.getHeight());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(destImg,requestDto.getProfileImage().getContentType().split("/")[1], out);
            log.info(requestDto.getProfileImage().getContentType().split("/")[1]);

            byte[] imageByte = out.toByteArray();
            out.close();
            MultipartFile multipartFile = new ConvertToMultipartFile(imageByte, "CROP", requestDto.getProfileImage().getOriginalFilename(), requestDto.getProfileImage().getContentType(), imageByte.length);

            String oldObjectKey = user.getProfileImageUrl();
            profileImageUrl = saveFile(multipartFile, user.getId(), oldObjectKey);
            //profileImageUrl = saveFile(requestDto.getProfileImage(), user.getId());
        }

        user.updateInfo(requestDto.getNickname(), requestDto.getGithubLink(),
                        requestDto.getIntroduce(), profileImageUrl);

        System.out.println("user.getProfileImageUrl():"+ user.getProfileImageUrl());

        userRepository.save(user);

        //프로필 이미지 첨부 안했으면
        if (requestDto.getProfileImage() == null){
            return UserInfoResponseDto.builder()
                    .user(UserInfoResponseDto.UserBody.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .githubLink(user.getGithubLink())
                    .profileImageUrl(null)
                    .introduce(user.getIntroduce())
                    .build())
             .build();
        }else{
            return UserInfoResponseDto.builder()
                    .user(UserInfoResponseDto.UserBody.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .githubLink(user.getGithubLink())
                    .profileImageUrl(interviewGeneralService.generateProfileImageUrl(user.getProfileImageUrl()))
                    .introduce(user.getIntroduce())
                    .build())
            .build();
        }
    }

    public File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename()); //서버 루트폴더에 저장됨
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    //이미지 파일 여부 image/gif, image/png, image/jpeg, image/bmp, image/webp  //(jpg등 테스트예정)
    private void isImageFile(MultipartFile profileImage) {
        Boolean isImage = profileImage.getContentType().split("/")[0].equals("image");
        Boolean isGif = profileImage.getContentType().equals("image/gif");
        if ((isImage == false) || (isGif == true)) {
            log.info("isImageFile() >> 프로필 이미지는 png, jpg, bmp, webp 확장자만 가능합니다.");
            throw new IllegalArgumentException("프로필 이미지는 png, jpg, bmp, webp 확장자만 가능합니다.");
        }
    }

    private String saveFile(MultipartFile multipartFile, Long userId, String oldObjectKey) throws IOException {

        //임시 폴더 생성(지정된 폴더가 없을때만 폴더 생성으로 변경?)
        String dir = Files.createTempDirectory("tempDir").toFile().getAbsolutePath();//EC2 경로 테스트

        File file = new File(dir + File.separator + multipartFile.getOriginalFilename());

        String fileName = file.getName();
        String savedFileNameWithPath = String.valueOf(file.getCanonicalFile());
        System.out.println("저장될 파일의 경로 포함 파일명: " + savedFileNameWithPath);

        //MultipatrFile클래스의 getBytes()로 multipartFile의 데이터를 바이트배열로 추출한 후, FileOutputStream클래스의 write()로 파일을 저장
        try (OutputStream os = new FileOutputStream(file)) {
            //파일 저장
            os.write(multipartFile.getBytes());

            //S3로 업로드
            String objectKey = sendToS3(file, userId, oldObjectKey, fileName);
            os.close();

            //업로드 성공시, 서버에 생성한 폴더,파일 삭제
            Path filePath = Paths.get(savedFileNameWithPath);
            System.out.println("savedFileNameWithPath: "+savedFileNameWithPath);
            Files.delete(filePath);
            Path directoryPath = Paths.get(dir);
            Files.delete(directoryPath);

            return objectKey;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to save file", e);
        }
    }

    private String sendToS3(File file, Long userId, String oldObjectKey, String fileName) throws IOException {

        String suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"));
        String s3Folder = "profileImg/";
        String objectKey = s3Folder + "userId-" + userId + "-" + suffix + "-" + fileName;

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .build();

            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            s3Client.putObject(new PutObjectRequest(bucket, objectKey, file));

            //S3에서 기존 프로필 이미지 삭제
            if (oldObjectKey != null){
                s3Client.deleteObject(bucket, oldObjectKey);
            }

            log.info("OBJECT KEY " + objectKey + " CREATED IN BUCKET " + bucket);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectKey;
    }
}
