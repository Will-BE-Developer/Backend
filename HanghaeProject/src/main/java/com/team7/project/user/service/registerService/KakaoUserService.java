package com.team7.project.user.service.registerService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team7.project.interview.service.InterviewGeneralService;
import com.team7.project.user.dto.KakaoUserInfoDto;
import com.team7.project.user.model.Role;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

//https://kauth.kakao.com/oauth/authorize?client_id=본인의 REST API키&redirect_uri=http://localhost:8080/user/kakao/callback&response_type=code


    @Service
    @Slf4j
    @RequiredArgsConstructor
    public class KakaoUserService {
        private final PasswordEncoder passwordEncoder;
        private final UserRepository userRepository;
        private final InterviewGeneralService interviewGeneralService;

//        @Autowired
//        public KakaoUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//            this.userRepository = userRepository;
//            this.passwordEncoder = passwordEncoder;
//        }

        public User kakaoLogin(String code) throws JsonProcessingException {
            log.info("Contorller : KAKAO_LOGIN >> 1. 인가코드로 액세스 토큰 요청");
            String accessToken = getAccessToken(code);

            log.info("Contorller : KAKAO_LOGIN >> 2. 액세스 토큰으로 카카오 사용자 정보 가져오기");
            KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);


            log.info("Contorller : KAKAO_LOGIN >> 3. 카카오 사용자 정보로 필요시 회원가입");
            User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo, accessToken);

            log.info("Contorller : KAKAO_LOGIN >> 4.강제 로그인 처리");
            forceLogin(kakaoUser);
            return kakaoUser;
        }
        private String getAccessToken(String code) throws JsonProcessingException {
            log.info("Contorller : GET_ACCESS_TOKEN>> 카카오 로그인 엑세스 토큰 발급 중입니다...");
            log.info("Contorller : GET_ACCESS_TOKEN>> 인가코드 : {}",code);
            // HTTP Header 생성
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            log.info("Contorller : GET_ACCESS_TOKEN>> HTTP Body를 생성중입니다...");
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", "95272555e8189d2f079be8adc9c37e4f");
            body.add("redirect_uri", "https://willbedeveloper.com/user/kakao/callback");
            body.add("client_secret","oAfSkjWSsZb7DoeYcffn4XDYf8eMmgIr");
            body.add("code", code);

            log.info("Contorller : GET_ACCESS_TOKEN>> HTTP 요청을 보내는 중입니다...");
            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                    new HttpEntity<>(body, headers);
            RestTemplate rt = new RestTemplate();
            ResponseEntity<String> response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );


            log.info("Contorller : GET_ACCESS_TOKEN>> 엑세스 토큰을 파싱중입니다.");
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        }

        private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
            // HTTP Header 생성
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            log.info("Contorller : GET_KAKAO_USER_INFO >>카카오에서 사용자 정보를 불러오는 중입니다...");
            // HTTP 요청 보내기
            HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
            RestTemplate rt = new RestTemplate();
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoUserInfoRequest,
                    String.class
            );

            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Long id = jsonNode.get("id").asLong();
            String provider = "kakao";
            String nickname;
            String imageUrl;

            String email = jsonNode.get("kakao_account")
                    .get("email").asText();

            try {
                nickname = jsonNode.get("properties")
                        .get("nickname").asText();
            }catch(NullPointerException e){
                long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                nickname = "윌비@" + number;
            }
            try {
                imageUrl = jsonNode.get("properties").get("profile_image").asText();
            }catch (NullPointerException e){
                imageUrl = null;
            }

            return new KakaoUserInfoDto(id, nickname, email,provider,imageUrl);
        }

        private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo, String accessToken) {
            log.info("Contorller : REGISTER_KAKAO_USER_IF_NEEDED >> 서버에 가입된 사용자가 있는지 조회중입니다...");
            String provider = kakaoUserInfo.getProvider();
            String email = kakaoUserInfo.getEmail();

            //가입된 카카오 사용자가 있는지 조회한다.
            User kakaoUser = userRepository.findByEmailAndProvider(email,provider)
                    .orElse(null);

            //가입된 카카오 사용자가 없다면, 가입을 진행한다
            if (kakaoUser == null) {
                log.info("Contorller : REGISTER_KAKAO_USER_IF_NEEDED >> 카카오 가입자가 없으므로 회원가입을 진행합니다.");
                // username: kakao nickname
                String nickname = kakaoUserInfo.getNickname();

                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // role: 일반 사용자
                Role role = Role.GUEST;

                kakaoUser = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .nickname(nickname)
                        .role(Role.GUEST)
                        .provider(provider)
                        .profileImageUrl(kakaoUserInfo.getImageUrl())
                        .isDeleted(false)
                        .isValid(true)
                        .token(accessToken)
                        .build();
                userRepository.save(kakaoUser);
            }
            //가입된 카카오 사용자가 있다면, 업데이트를 진행한다.
            kakaoUser.updateInfo(kakaoUserInfo.getNickname(), kakaoUserInfo.getImageUrl());

            return kakaoUser;
        }

        private void forceLogin(User kakaoUser) {
//            UserDetails userDetails = new UserDetailsImpl(kakaoUser);
            log.info("Contorller : FORCE_LOGIN >> 스프링 시큐리티에 유저를 등록시키는 중입니다...");
            Authentication authentication = new UsernamePasswordAuthenticationToken(kakaoUser, null, kakaoUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        public void kakaoLogout(String accessToken){
            String reqURL = "https://kapi.kakao.com/v1/user/logout";
            try{
                URL url = new URL(reqURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                log.info("KAKAOUSERSERVICE >> KAKALO LOGOUT  >> accessToken : {}", accessToken );
                conn.setRequestProperty("Authorization","Bearer "+ accessToken);

                int responseCode = conn.getResponseCode();
                log.info("responseCode : " + responseCode);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String result = "";
                String line = "";

                while((line = br.readLine()) != null) {
                    result += line;
                }
                log.info("result is {}",result);

            }catch(IOException e){
                e.printStackTrace();
            }

        }
    }

