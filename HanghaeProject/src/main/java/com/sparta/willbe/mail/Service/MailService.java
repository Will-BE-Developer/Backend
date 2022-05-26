package com.sparta.willbe.mail.Service;

import com.sparta.willbe.advice.Success;
import com.sparta.willbe.mail.template.MailTemplate;
import com.sparta.willbe.mail.utils.EmailUtils;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements EmailUtils{

    private final JavaMailSender sender;
    private MailTemplate mailTemplate = new MailTemplate();
    private String htmlTemplate;

    //TODO: 서버에 올릴때 클래스 패스 or 서버에 .html파일 넣어서 바로 읽던지 하기
    @PostConstruct
    public void init() throws IOException{
        final File file = ResourceUtils.getFile("./mail/mailtemplate.html");

        log.info("JAVA  class path : {}",System.getProperty("java.class.path"));

        final InputStream inputStream = new FileInputStream(file);
        final byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
        htmlTemplate = new String(byteData, StandardCharsets.UTF_8);
    }
    private String changeContents(HashMap param, String formatString) throws Exception{
        StrSubstitutor sub = new StrSubstitutor(param);
        return sub.replace(formatString);
    }

    private String createBodyMailText(String token, String email, String nickname) throws Exception {

        HashMap mailParamMap = new HashMap();
        mailParamMap.put("USER_NAME", nickname);
        mailParamMap.put("USER_EMAIL", email);
        mailParamMap.put("VALIDATION_TOKEN", token);

        String mailContents = changeContents(mailParamMap, htmlTemplate);

        return mailContents;
    }
    @Override
    public ResponseEntity<Success> sendEmail(String toEmail, String token, String nickname){

        ResponseEntity<Success> result ;

        try{
            File f1 = new File("");
             MimeMessage message = sender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String body = createBodyMailText(token,toEmail,nickname);
            helper.setTo(toEmail);
            helper.setSubject("WILL_BE : 이메일 인증을 완료해 주세요\uD83D\uDE18");
            helper.setText(body,true);

            FileDataSource fileDataSource = new FileDataSource("./mail/logo.png");
            helper.addInline("logo",fileDataSource);
            sender.send(message);
            result = new ResponseEntity<Success>(new Success(true, "메일 발송 성공!"),HttpStatus.OK);
        }catch (MessagingException e){
            e.printStackTrace();
            Sentry.captureException(e);
            result = new ResponseEntity<Success>(new Success(false, "메일 발송 실패!"),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.captureException(e);
            result = new ResponseEntity<Success>(new Success(false, "메일 발송 실패!"),HttpStatus.BAD_REQUEST);
        }

        return result;
    }
}
