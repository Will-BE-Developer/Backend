package com.sparta.willbe.interview.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.interview.model.Interview;
import com.sparta.willbe.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.activation.FileDataSource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InterviewConvertService {

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    private final AmazonS3Client amazonFullS3Client;
    private final InterviewRepository interviewRepository;

    @Async
    public void onAsync() {
        log.info("onAsync");
    }

    @Async
    public String webmToMp4(String objectKey, Long interviewId) {
        String downloadPath = objectKey.replace("videos/", "./");
        String uploadPath = downloadPath.replace(".webm", ".mp4");
        String newObjectKey = objectKey.replace(".webm", ".mp4");
        download(objectKey, downloadPath);

        try {
            convert(downloadPath, uploadPath, interviewId);
        } catch (Exception e) {
            log.error("CONVERT FAIL INTERVIEW: " + interviewId);
            interviewRepository.deleteById(interviewId);
            return newObjectKey;
        }

        upload(objectKey, uploadPath);

        try {
            Files.deleteIfExists(Paths.get(downloadPath));
            Files.deleteIfExists(Paths.get(uploadPath));
        } catch (Exception e) {
            log.error("NO SUCH FILE: " + downloadPath);
            interviewRepository.deleteById(interviewId);
            return newObjectKey;
        }


        log.info("OBJECT KEY " + objectKey + " CONVERT IN " + newObjectKey);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(ErrorMessage.NOT_FOUND_DRAFT::throwError);
        interview.convertVideo();

        return newObjectKey;
    }

    public void download(String objectKey, String downloadPath) {
        log.info("OBJECT KEY " + objectKey + " DOWNLOADED IN PATH " + downloadPath);
        try {
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            InputStream in = amazonFullS3Client.getObject(new GetObjectRequest(bucket, objectKey)).getObjectContent();
            Files.copy(in, Paths.get(downloadPath));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void convert(String webmPath, String mp4Path, Long interviewId) throws IOException {
        FFmpeg fFmpeg = new FFmpeg("/usr/bin/ffmpeg");
        FFprobe fFprobe = new FFprobe("/usr/bin/ffprobe");
        FileDataSource fileDataSource = new FileDataSource(webmPath);
        File webmFile = fileDataSource.getFile();
        File mp4File = new File(mp4Path);

        FFmpegBuilder builder = new FFmpegBuilder()
                .overrideOutputFiles(true)
                .setInput(webmFile.getPath())
                .addOutput(mp4File.getPath())
                .setVideoCodec("h264")
                .setVideoFrameRate(24, 1)
                .done();

        log.info("EXEC FFMPEG " + builder.toString());

        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
        FFmpegJob job = executor.createJob(builder);
        job.run();

    }

    public void upload(String objectKey, String uploadFilePath) {
        String newObjectKey = objectKey.replace(".webm", ".mp4");

        try {
            FileDataSource fileDataSource = new FileDataSource(uploadFilePath);
            File file = fileDataSource.getFile();

            amazonFullS3Client.putObject(new PutObjectRequest(bucket, newObjectKey, file));
            if (amazonFullS3Client.doesObjectExist(bucket, objectKey)) {
                amazonFullS3Client.deleteObject(bucket, objectKey);
            }

            log.info("OBJECT KEY " + objectKey + " CREATED IN BUCKET " + bucket);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}