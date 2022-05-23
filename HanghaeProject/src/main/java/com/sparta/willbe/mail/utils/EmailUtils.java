package com.sparta.willbe.mail.utils;

import org.springframework.http.ResponseEntity;


public interface EmailUtils {
    ResponseEntity sendEmail(String toEmail, String token, String nicknmae);
}
