package com.team7.project.mail.Service;

import com.team7.project.advice.RestException;
import com.team7.project.mail.template.MailTemplate;
import com.team7.project.mail.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements EmailUtils{

    private final JavaMailSender sender;


    private MailTemplate mailTemplate = new MailTemplate();

    @Override
    public RestException sendEmail(String toEmail, String token, String nickname){
        RestException result = new RestException(null,null);
        try{
            URL url = new URL("https://support.taguchi.com.au/knowledge-base/campaigns-and-activities/content/why-is-the-text-purple-in-gmail.html#:~:text=It%20is%20caused%20by%20Gmail,purple%20coloured%20text%20will%20disappear.");
            File file = Paths.get(url.toURI()).toFile();
            File f1 = new File("");
             MimeMessage message = sender.createMimeMessage();
             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String body=mailTemplate.getTemplate(token,toEmail,nickname);
            helper.setTo(toEmail);
            helper.setSubject("WILL_BE : 이메일 인증을 완료해 주세요\uD83D\uDE18");
            helper.setText(body,true);
            helper.addInline("logo",new FileDataSource(file));
            sender.send(message);
            result.setMessage("메일 발송 성공");
            result.setHttpStatus(HttpStatus.OK);
        }catch (MessagingException | MalformedURLException | URISyntaxException e){
            e.printStackTrace();
            result.setMessage("메일 발송 실패");
            result.setHttpStatus(HttpStatus.BAD_REQUEST);
        }


        return result;
    }
}
