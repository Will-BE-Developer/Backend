package com.team7.project.user.service;

import com.team7.project.advice.RestException;
import com.team7.project.mail.Service.MailService;
import com.team7.project.security.jwt.JwtTokenProvider;
import com.team7.project.user.dto.RegisterRequestDto;
import com.team7.project.user.model.Role;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRegistryService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User registerUser(RegisterRequestDto requestDto) {

        log.info("SIGN_UP() >> registerUser >> Nickname 유무확인 ");

        if(requestDto.getNickname() == null) {
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            String randomGenName = "윌비@" + number;
            log.info("SIGN_UP() >> registerUser >> Nickname 생성 {}", randomGenName.toString());
            requestDto.setNickname(randomGenName.toString());
        }
        UUID uuid = UUID.randomUUID();
        log.info("GENERATE UUID for EMAIL VaLIDATION TOKEN : {} ",uuid.toString());
        log.info("SIGN_UP() >> registerUser >> UserRepository 에 유저 정보 생성후 저장중...");
            User user = userRepository.saveAndFlush(User.builder()
                    .email(requestDto.getEmail())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .nickname(requestDto.getNickname())
                    .role(Role.GUEST)
                    .provider("email")
                    .token(uuid.toString())
                    .isDeleted(false)
                    .isValid(false)
                    .build());
        log.info("SIGN_UP() >> registerUser() >>  return user ");
            return user;
    }

    public boolean isEmailExist(String email){
        if(userRepository.findByEmail(email).isPresent()){
            log.info("SIGN_UP() >> isEamilExist() >> 이메일 사용 가능 ");
            return true;
        }else {
            log.info("SIGN_UP() >> isEamilExist() >> 이메일 사용 불가능 ");
            return false;
        }
    }

}
