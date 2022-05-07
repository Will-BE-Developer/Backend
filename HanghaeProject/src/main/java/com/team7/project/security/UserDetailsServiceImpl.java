package com.team7.project.security;

import com.team7.project.user.model.User;
import com.team7.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

//스프링 시큐리티 구조에서
//DB에서 UserDetailsService에게 패스워드 아이디, 권한 등을 넘겨 주고 UserDeails로 전달되어 받음
//Userdetails는 Users로 구현되어있음.
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    //TODO:에러 메세지 출력 확인
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = User.builder()
                .nickname("testUser")
                .email("hello@tester.com")
                .password("1234")
                .profileImageUrl("pic")
                .build();
        return user;
    }
}
