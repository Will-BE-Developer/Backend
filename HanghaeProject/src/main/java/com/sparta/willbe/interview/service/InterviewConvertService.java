/*
* NOTE THAT THIS IS IMPLEMENTED IN BRANCH "feature/convert"
*/


//package com.team7.project.interview.service;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.GetObjectRequest;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.team7.project.advice.ErrorMessage;
//import com.team7.project.interview.model.Interview;
//import com.team7.project.interview.repository.InterviewRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.bramp.ffmpeg.FFmpeg;
//import net.bramp.ffmpeg.FFmpegExecutor;
//import net.bramp.ffmpeg.builder.FFmpegBuilder;
//import net.bramp.ffmpeg.job.FFmpegJob;
//import org.bytedeco.javacpp.Loader;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import javax.activation.FileDataSource;
//import javax.transaction.Transactional;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//@Slf4j
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class InterviewConvertService {
//
//    @Value("${cloud.aws.credentials.access-key-upload}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secret-key-upload}")
//    private String secretKey;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Value("${cloud.aws.s3.bucket}")
//    public String bucket;
//
//    String ffmpegPath = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
//
//    private final InterviewRepository interviewRepository;
//
//    @Async
//    public String webmToMp4(String objectKey, Long interviewId) throws IOException {
//        String downloadPath = objectKey.replace("videos/", "./");
//        String uploadPath = downloadPath.replace(".webm",".mp4");
//        String newObjectKey = objectKey.replace(".webm",".mp4");
//        download(objectKey, downloadPath);
//
//        convert(downloadPath, uploadPath);
//
//        upload(newObjectKey, uploadPath);
//
//        Files.deleteIfExists(Paths.get(downloadPath));
//        Files.deleteIfExists(Paths.get(uploadPath));
//
//        log.info("OBJECT KEY " + objectKey + " CONVERT IN " + newObjectKey);
//
//        Interview interview = interviewRepository.findById(interviewId)
//                .orElseThrow(ErrorMessage.NOT_FOUND_DRAFT::throwError);
//        interview.convertVideo();
//
//        return newObjectKey;
//    }
//
//    public void download(String objectKey, String downloadPath){
//        log.info("OBJECT KEY " + objectKey + " DOWNLOADED IN PATH " + downloadPath);
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//        try {
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                    .withRegion(region)
//                    .build();
//
//            java.util.Date expiration = new java.util.Date();
//            long expTimeMillis = expiration.getTime();
//            expTimeMillis += 1000 * 60 * 60;
//            expiration.setTime(expTimeMillis);
//
//            File file = new File(downloadPath);
//
//            InputStream in = s3Client.getObject(new GetObjectRequest(bucket, objectKey)).getObjectContent();
//            Files.copy(in, Paths.get(downloadPath));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void convert(String webmPath, String mp4Path) throws IOException {
//        FileDataSource fileDataSource = new FileDataSource(webmPath);
//        File webmFile = fileDataSource.getFile();
//        File mp4File = new File(mp4Path);
//
//        FFmpegBuilder builder = new FFmpegBuilder()
//                .overrideOutputFiles(true)
//                .setInput(webmFile.getPath())
//                .addOutput(mp4File.getPath())
//                .setVideoCodec("h264")
//                .setVideoFrameRate(24,1)
//                .done();
//
//        log.info("EXEC FFMPEG " + builder.toString());
//
//        FFmpegExecutor executor = new FFmpegExecutor(new FFmpeg(ffmpegPath));
//        FFmpegJob job = executor.createJob(builder);
//        job.run();
//
//    }
//
//    public void upload(String objectKey, String uploadFilePath){
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//        try {
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                    .withRegion(region)
//                    .build();
//
//            java.util.Date expiration = new java.util.Date();
//            long expTimeMillis = expiration.getTime();
//            expTimeMillis += 1000 * 60 * 60;
//            expiration.setTime(expTimeMillis);
//
//            FileDataSource fileDataSource = new FileDataSource(uploadFilePath);
//            File file = fileDataSource.getFile();
//
//            s3Client.putObject(new PutObjectRequest(bucket, objectKey, file));
//
//            log.info("OBJECT KEY " + objectKey + " CREATED IN BUCKET " + bucket);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}