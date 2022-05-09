package com.team7.project.security.jwt;

import com.team7.project.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class JwtTokenProvider {

    private static String SECRET_KEY = "4dW2Ri6fZjsSjgldfYe8soispI6QoqCnvi8oewMS2rvbeW5Swo";


    private static final long SEC = 1000L;
    private static final long MINUTE = 60 * SEC;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    private final long ACCESS_TOKEN_VALID_TIME = HOUR ;   // 1 시간

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    // JWT 토큰 생성
    public String createAccessToken(String userPk) {
        return this.createToken(userPk, ACCESS_TOKEN_VALID_TIME);
    }

    public String createToken(String userPk, long tokenValid) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValid)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

    }

    // JWT 토큰에서 인증 정보 조회
    //loadUserByUsername : userrepository에서 username으로 사용자 조회하는 기능
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "Autheorization" : "TOKEN값'
    public String resolveAccessToken(HttpServletRequest request) {

        //request가 들어오는지 확인하는 sysout
//        System.out.println("Authorization ::: "+request.getHeader("Authorization"));
        //Header에 토큰이 존재 하면 가져오고 아니면 null 반환
        if (request.getHeader("Authorization") != null){
            System.out.println("Authorization ::: "+request.getHeader("Authorization"));
            return request.getHeader("Authorization");
        }
        return null;
    }


    public static boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}

