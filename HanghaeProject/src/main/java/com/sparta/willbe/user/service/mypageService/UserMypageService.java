package com.sparta.willbe.user.service.mypageService;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.sparta.willbe.interview.service.InterviewService;
import com.sparta.willbe.user.dto.UserInfoResponseDto;
import com.sparta.willbe.user.dto.UserRequestDto;
import com.sparta.willbe.user.exception.*;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.repository.UserRepository;
import io.sentry.Sentry;
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
    private final InterviewService interviewService;

    @Value("${cloud.aws.credentials.access-key-upload}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key-upload}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Value("${spring.servlet.multipart.maxFileSize}")
    private String maxFileSize = "";

    private String basicProfile = "profileImg/100.jpeg";

    boolean isStringEmpty(String nickname) {
        return nickname == null || nickname.trim().isEmpty();
    }

    @Transactional
    public UserInfoResponseDto save(UserRequestDto requestDto, User user, String profileImageString) throws IOException {
        String profileImageUrl = null;

        userRepository.findById(user.getId()).orElseThrow(
                () -> new UserNotFoundException());

        //???????????? ?????? ????????????, ?????? ????????? ??????
        if(isStringEmpty(requestDto.getNickname())){
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            String randomGenName = "??????@" + number;
            requestDto.setNickname(randomGenName);
        }

        log.info("editUserInfo() >> ????????? ?????? ????????? ????????? URL : {}", user.getProfileImageUrl());

        //???????????? ????????? ?????? ???, ????????? ?????? ??? S3??? ????????????(?????? ???????????? null->??????????????? default?????????)
        if (requestDto.getProfileImage() != null) {
            //????????? ?????? ??????
            isImageFile(requestDto.getProfileImage(), user.getId());

            //????????? ?????? ??????
            checkFileSize(requestDto.getProfileImage(), user.getId());

            //image ?????? ??????(for ??????)
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
            //????????? ??????
            BufferedImage cropImg = Scalr.crop(originalImage,
                    (originalImageWidth-newImageWidth)/2,
                    (originalImageHeight-newImageHeight)/2,
                    newImageWidth, newImageHeight);
            //????????? ????????????
            BufferedImage destImg = Scalr.resize(cropImg, dw, dh);

            log.info("after crop & resizing x : {} ", destImg.getWidth());
            log.info("after crop & resizing y : {} ", destImg.getHeight());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(destImg,requestDto.getProfileImage().getContentType().split("/")[1], out);
            log.info(requestDto.getProfileImage().getContentType().split("/")[1]);

            byte[] imageByte = out.toByteArray();
            out.close();
            Files.deleteIfExists(Paths.get(getFile.getPath()));
            MultipartFile multipartFile = new ConvertToMultipartFile(imageByte, "CROP", requestDto.getProfileImage().getOriginalFilename(), requestDto.getProfileImage().getContentType(), imageByte.length);

            String oldObjectKey = user.getProfileImageUrl();
            profileImageUrl = saveFile(multipartFile, user.getId(), oldObjectKey);
            //profileImageUrl = saveFile(requestDto.getProfileImage(), user.getId());

        }
        
        if (profileImageString != null && profileImageString.equals("undefined")) {
            user.updateInfo(requestDto.getNickname(), requestDto.getGithubLink(),
                    requestDto.getIntroduce(), user.getProfileImageUrl());
        // ?????? ?????? or ?????????
        } else {
            user.updateInfo(requestDto.getNickname(), requestDto.getGithubLink(),
                    requestDto.getIntroduce(), profileImageUrl);

        }

        userRepository.save(user);

        //????????? ????????? ?????? ????????????(undefined or null)
        //?????? ??? ????????? ??????
        if (profileImageString != null && profileImageString.equals("undefined")) {
            log.info("editUserInfo() >> save() >> ????????? ?????? ??? ????????? ???????????????.(undefined)");
            return UserInfoResponseDto.builder()
                    .user(UserInfoResponseDto.UserBody.builder()
                            .id(user.getId())
                            .nickname(user.getNickname())
                            .githubLink(user.getGithubLink())
                            .profileImageUrl(interviewService.getProfileImageUrl(user.getProfileImageUrl()))
                            .introduce(user.getIntroduce())
                            .build())
                    .build();
        } else if (requestDto.getProfileImage() == null){
            //?????? ?????? ??????
            log.info("editUserInfo() >> save() >> ????????? ?????? ?????? ??? ????????? ???????????????.(null)");
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
            //?????? ????????? ??????
            log.info("editUserInfo() >> save() >> ????????? ?????? ??? ????????? ???????????????.");
            return UserInfoResponseDto.builder()
                    .user(UserInfoResponseDto.UserBody.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .githubLink(user.getGithubLink())
                    .profileImageUrl(interviewService.getProfileImageUrl(user.getProfileImageUrl()))
                    .introduce(user.getIntroduce())
                    .build())
            .build();
        }
    }

    public File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename()); //?????? ??????????????? ?????????
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    //????????? ?????? ??????, ????????? ?????? image/gif, image/png, image/jpeg(=jpg) (image/bmp, image/webp ??????)
    private void isImageFile(MultipartFile profileImage, Long userId) {
        String contentType = profileImage.getContentType();

        boolean isImage = contentType.split("/")[0].equals("image");
        String extension = contentType.split("/")[1];

        if ((isImage == false) || extension.equals("webp") || extension.equals("bmp")) {
            log.error("????????? ????????? ?????? ??????({}) ??????(userId: {})", contentType, userId);
            throw new ImageFileTypeException();
        }
    }

    private void checkFileSize(MultipartFile profileImage, Long userId) {
        if (profileImage.isEmpty()) {
            log.error("????????? ????????? ?????? 0 byte (userId: {})", userId);
            throw new ImageFileSizeZeroException();
        }
        double bytes = profileImage.getSize();
        double kilobytes = Math.round(bytes / 1024*1000.0)/1000.0;
        double megabytes = Math.round(kilobytes / 1024*1000.0)/1000.0;
        int MaximumSize = Integer.parseInt(maxFileSize.replace("MB",""));
        if (megabytes >= MaximumSize) {
            log.error("????????? ????????? 5MB ?????? :: userId: {}, ???????????????: {}MB", userId, megabytes);
            throw new ImageFileSizeOverException();
        }
    }

    private String saveFile(MultipartFile multipartFile, Long userId, String oldObjectKey) throws IOException {

        //?????? ?????? ??????
        String dir = Files.createTempDirectory("tempDir").toFile().getAbsolutePath();

        File file = new File(dir + File.separator + multipartFile.getOriginalFilename());

        String fileName = file.getName();
        String savedFileNameWithPath = String.valueOf(file.getCanonicalFile());
        log.info("????????? ????????? ?????? ?????? ?????????: {}", savedFileNameWithPath);

        //MultipatrFile???????????? getBytes()??? multipartFile??? ???????????? ?????????????????? ????????? ???, FileOutputStream???????????? write()??? ????????? ??????
        try (OutputStream os = new FileOutputStream(file)) {
            //?????? ??????
            os.write(multipartFile.getBytes());

            //S3??? ?????????
            String objectKey = sendToS3(file, userId, oldObjectKey, fileName);
            os.close();

            //????????? ?????????, ????????? ????????? ??????,?????? ??????
            Path filePath = Paths.get(savedFileNameWithPath);
            Files.delete(filePath);
            Path directoryPath = Paths.get(dir);
            Files.delete(directoryPath);

            return objectKey;
        } catch (Exception e) {
            log.error("saveFile() >> ????????? ????????? ?????? ??????(userId: {}, fileName: {})", userId, fileName);
            throw new ImageSaveFailException();
        }
    }

    private String sendToS3(File file, Long userId, String oldObjectKey, String fileName) {

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

            //S3?????? ?????? ????????? ????????? ??????
            try{
                if (oldObjectKey != null){
                    s3Client.deleteObject(bucket, oldObjectKey);
                }
            } catch (Exception e) {
                log.error("S3?????? ?????? ????????? ????????? ?????? ??????(userId: {}) - {}", userId, e.getMessage());
            }
            log.info("OBJECT KEY : {}, CREATED IN BUCKET : {}", objectKey, bucket);
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new ImageSendToS3Exception();
        }
        return objectKey;
    }
}
