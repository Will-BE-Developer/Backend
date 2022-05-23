package com.sparta.willbe.user.service.registerService;

import com.sparta.willbe.advice.ErrorMessage;
import com.sparta.willbe.user.model.User;
import com.sparta.willbe.security.jwt.JwtTokenProvider;
import com.sparta.willbe.security.jwt.TokenResponseDto;
import com.sparta.willbe.user.dto.request.LoginRequestDto;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    //login 성공시에 TokenResponseDto를 반환하면 controller 에서 헤더에 실어 보내준다.
    public User login(LoginRequestDto requestDto) {
        //가입되지 않은 회원정보로 로그인 시도 하면 안됨
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> ErrorMessage.NOT_FOUND_USER.throwError());

        //회원정보와 작성한 비밀번호가 일치하지 않으면 안됨!
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw ErrorMessage.NOT_FOUND_PASSWORD.throwError();
        }
        if(user.getIsValid() == false){
            throw ErrorMessage.INVALID_EMAIL.throwError();
        }

        return user;
    }

    @Transactional
    public TokenResponseDto giveToken(String email){
        String accessToken = jwtTokenProvider.createAccessToken(email);
        return TokenResponseDto.builder()
                .Authorization("BEARER " + accessToken)
                .build();
    }
    @Transactional
    public void logout(HttpServletRequest request){
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public User deleteUser(User user){
        log.info("DELETE_USER >> delete_user_(service) >> {}에 대해 deleted 접근 중... 현재 isDeleted : {}",
                user.getNickname(),user.getIsDeleted());
        User deleteThis = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> ErrorMessage.NOT_FOUND_USER.throwError());
        userRepository.delete(deleteThis);
        return deleteThis;
    }
    @Transactional
    public User validateUser(String email, String token){
        User validatingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> ErrorMessage.NOT_FOUND_USER.throwError());
        log.info("CONTORLLER >> SERVICE >> VALIDATE_USER : {} ", validatingUser.getNickname());
        if(validatingUser.getToken().equals(token)) {
            validatingUser.isEmailvalidUser(true);
            log.info("CONTORLLER >> SERVICE >> VALIDATE_USER change is valid to : {} ", validatingUser.getIsValid());
        }else{
            throw ErrorMessage.INVALID_TOKEN.throwError();
        }

        return validatingUser;
    }

}
