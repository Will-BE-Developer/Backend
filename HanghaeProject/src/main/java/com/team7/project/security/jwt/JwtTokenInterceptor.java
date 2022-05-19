package com.team7.project.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) throws IOException{
        System.out.println("JwtToken 호출");
        String accessToken = request.getHeader("Authorization");
        try {
            accessToken = accessToken.replace("BEARER ", "");
        }catch (NullPointerException e){
            accessToken = null;
        }
        if(accessToken!= null && !jwtTokenProvider.validateToken(accessToken)) {
            response.setStatus(401);
            response.setHeader("Authorization", accessToken);
            response.setHeader("msg", "Token is not valid. Server block the request");
            return false;
        }

        return true;
    }
}
