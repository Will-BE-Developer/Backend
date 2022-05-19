package com.team7.project.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object Handler) throws IOException{
        System.out.println("JwtToken 호출");
        String accessToken = request.getHeader("Authorization");
        try {
            log.info("ACCESS TOKEN: {}",accessToken);
            accessToken = accessToken.replace("BEARER ", "");
        }catch (NullPointerException e){
            log.info("ACCESS TOKEN: {} >>NULL_POINT_EXCEPTION",accessToken);
            return true;
        }

        if (accessToken != null && !jwtTokenProvider.validateToken(accessToken)) {
            log.info("ACCESS TOKEN: {} >>FALSE STATEMENT ",accessToken);
            log.info("NOT VALIDATION TOKEN : {}",!jwtTokenProvider.validateToken(accessToken));
            log.info("ACCESSTOKNE iS NULL : {}", accessToken != null);
            response.setStatus(401);
            response.setHeader("Authorization", accessToken);
            response.setHeader("msg", "Token is not valid. Server block the request");
            return false;
        }

        return true;
    }
}
