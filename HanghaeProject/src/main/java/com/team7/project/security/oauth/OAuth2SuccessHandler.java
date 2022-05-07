package com.team7.project.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team7.project.security.jwt.JwtTokenProvider;
import com.team7.project.security.jwt.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final TokenService tokenService;
//    private final UserRequestMapper userRequestMapper;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        //UserDto userDto = userRequestMapper.toDto(oAuth2User);

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
        // 최초 로그인이라면 회원가입 처리를 한다.
        String targetUrl;
        log.info("토큰 발행 시작");
        //_______________수정 요함_____________
        //token generate 할때
        //user email로 토큰 생성되게 바꿔 줘야한다
        //___________________________________

        Token token = new Token(jwtTokenProvider.createAccessToken(oAuth2User.getName()));
        log.info("{}", token.getToken());
        targetUrl = UriComponentsBuilder.fromUriString("/")
//                .queryParam("token", "token")
                .build().toUriString();
        response.setHeader("Authorization",token.getToken().toString());
//        request.setAttribute("Authorization",token.getToken().toString());
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
