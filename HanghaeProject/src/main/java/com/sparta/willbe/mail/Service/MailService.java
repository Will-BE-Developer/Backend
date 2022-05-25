package com.sparta.willbe.mail.Service;

import com.sparta.willbe.advice.Success;
import com.sparta.willbe.mail.template.MailTemplate;
import com.sparta.willbe.mail.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements EmailUtils{


    private final JavaMailSender sender;
    private MailTemplate mailTemplate = new MailTemplate();
    private String htmlTemplate;

    @PostConstruct
    public void init() throws IOException{
        final File file = ResourceUtils.getFile("classpath:templates/mailtemplate.html");
        final InputStream inputStream = new FileInputStream(file);
        final byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
        htmlTemplate = new String(byteData, StandardCharsets.UTF_8);
    }

    private String createBodyMailText(String token, String email, String nickname){
        htmlTemplate.replaceAll("\\$\\{token}",token);
        htmlTemplate.replaceAll("\\$\\{email}",email);
        htmlTemplate.replaceAll("username",nickname);
        return htmlTemplate;
    }
    @Override
    public ResponseEntity<Success> sendEmail(String toEmail, String token, String nickname){

        ResponseEntity<Success> result ;

        try{
            File f1 = new File("");
             MimeMessage message = sender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String body=mailTemplate.getTemplate(token,toEmail,nickname);
//            String body = createBodyMailText(token,toEmail,nickname);
            helper.setTo(toEmail);
            helper.setSubject("WILL_BE : 이메일 인증을 완료해 주세요\uD83D\uDE18");
            helper.setText(body,true);

//            FileDataSource fileDataSource = new FileDataSource("./logo.png");
//            helper.addInline("logo",fileDataSource);
            sender.send(message);
            result = new ResponseEntity<Success>(new Success(true, "메일 발송 성공!"),HttpStatus.OK);
        }catch (MessagingException e){
            e.printStackTrace();
            result = new ResponseEntity<Success>(new Success(false, "메일 발송 실패!"),HttpStatus.BAD_REQUEST);
        }


        return result;
    }
}
