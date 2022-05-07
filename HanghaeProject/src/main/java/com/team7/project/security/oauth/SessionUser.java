/*
*인증된 사용자의 정보를 저장하는 클래스
*
*  */

package com.team7.project.security.oauth;

import com.team7.project.user.model.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;
    private String email;
    private String picture;

    public SessionUser(User user){
        this.name = user.getUsername();
        this.email = user.getEmail();
        this.picture = user.getProfileImageUrl();
    }
}
