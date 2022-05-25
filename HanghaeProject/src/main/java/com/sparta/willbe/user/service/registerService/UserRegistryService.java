package com.sparta.willbe.user.service.registerService;

import com.sparta.willbe.user.model.User;
import com.sparta.willbe.user.dto.request.RegisterRequestDto;
import com.sparta.willbe.user.model.Role;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public User registerUser(RegisterRequestDto requestDto) {

        log.info("SIGN_UP() >> registerUser >> Nickname 유무확인 ");

        if(requestDto.getNickname().isEmpty()) {
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
        if( userRepository.findByEmailAndIsValid(email,true).isPresent()){
            log.info("SIGN_UP() >> isEamilExist() >> 이메일 사용 불가능 ");
            return true;
        }else {
            log.info("SIGN_UP() >> isEamilExist() >> 이메일 사용 가능 ");
            return false;
        }
    }

}
