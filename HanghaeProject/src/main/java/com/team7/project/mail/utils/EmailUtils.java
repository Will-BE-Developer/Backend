package com.team7.project.mail.utils;

import com.team7.project.advice.RestException;

import java.util.Map;

public interface EmailUtils {
    RestException sendEmail(String toEmail, String token, String nicknmae);
}
