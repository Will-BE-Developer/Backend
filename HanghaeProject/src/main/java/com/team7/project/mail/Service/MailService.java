package com.team7.project.mail.Service;

import com.team7.project.advice.RestException;
import com.team7.project.mail.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService implements EmailUtils{

    @Autowired
    private JavaMailSender sender;

    @Override
    public RestException sendEmail(String toEmail, String token){
        RestException result = new RestException(null,null);
        try{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String body="<a href='http://localhost:8080/signin/validation?token="+
                token+"&email="+toEmail+"'>Click</a>";
            helper.setTo(toEmail);
            helper.setSubject("WILL_BE : 이메일 인증을 완료해 주세요:)");
            helper.setText(body,true);
            sender.send(message);
            result.setMessage("메일 발송 성공");
            result.setHttpStatus(HttpStatus.OK);
        }catch (MessagingException e){
            e.printStackTrace();
            result.setMessage("메일 발송 실패");
            result.setHttpStatus(HttpStatus.BAD_REQUEST);
        }
        return result;
    }
}
