package com.sparta.willbe.security;

import com.sparta.willbe.user.exception.UserNotFoundException;
import com.sparta.willbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

//스프링 시큐리티 구조에서
//DB에서 UserDetailsService에게 패스워드 아이디, 권한 등을 넘겨 주고 UserDetails로 전달되어 받음
//Userdetails는 Users로 구현되어있음.
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) {
       return userRepository.findByEmailAndIsDeletedFalseAndIsValidTrue(username)
               .orElseThrow(() -> new UserNotFoundException());
    }
}
