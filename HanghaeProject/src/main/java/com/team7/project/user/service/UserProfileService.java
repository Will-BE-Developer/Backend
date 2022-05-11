package com.team7.project.user.service;

import com.team7.project.advice.RestException;
import com.team7.project.security.jwt.JwtTokenProvider;
import com.team7.project.security.jwt.TokenResponseDto;
import com.team7.project.user.dto.LoginRequestDto;
import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, "가입되지 않은 이메일 입니다."));

        //회원정보와 작성한 비밀번호가 일치하지 않으면 안됨!
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RestException(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");
        }
        if(user.getIsValid() == false){
            throw new RestException(HttpStatus.BAD_REQUEST, "이메일 인증 후에 이용해주세요.");
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
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND,"회원이 존재하지 않습니다."));
        userRepository.delete(deleteThis);
//        deleteThis.setIsDeleted(true);
        return deleteThis;
    }
    @Transactional
    public User validateUser(String email, String token){
        User validatingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RestException(HttpStatus.NOT_FOUND,"회원이 존재하지 않습니다."));
        log.info("CONTORLLER >> SERVICE >> VALIDATE_USER : {} ", validatingUser.getNickname());
        if(validatingUser.getToken().equals(token)) {
            validatingUser.isEmailvalidUser(true);
            log.info("CONTORLLER >> SERVICE >> VALIDATE_USER change is valid to : {} ", validatingUser.getIsValid());
        }else{
            throw new RestException(HttpStatus.BAD_REQUEST,"유효한 토큰이 아닙니다");
        }

        return validatingUser;
    }

}
