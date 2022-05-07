/*
* oauth에서 entity를 생성하는 경우는
* 회원가입할때 한번 뿐이다
* */
package com.team7.project.security.oauth;

import com.team7.project.user.model.Role;
import com.team7.project.user.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickname;
    private String password;
    private String email;
    private String picture;
    private Boolean isValid;
    private String provider;
    private Boolean isDeleted;


    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String nickname, String password,
                           String email, String picture, Boolean isValid, String provider, Boolean isDeleted) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.picture = picture;
        this.isValid = isValid;
        this.provider = provider;
        this.isDeleted = isDeleted;
    }


    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String,Object> attributes){
        //(new!) kakao
        if("kakao".equals(registrationId)){
            return ofKakao("id", attributes);
        }
        // google
        return ofGoogle(userNameAttributeName, attributes);
    }
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .password((String) attributes.get("password"))
                .isValid(true)
                .isDeleted(false)
                .provider("google")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String,Object> attributes){
        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");
        log.info("**kakaoAccount Json ::: {}", kakaoAccount);

        return OAuthAttributes.builder()
                .nickname((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .password((String) attributes.get("password"))
                .isValid(true)
                .isDeleted(false)
                .provider("kakao")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        password = String.valueOf(UUID.randomUUID());
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .profileImageUrl(picture)
                .isValid(isValid)
                .isDeleted(isDeleted)
                .provider(provider)
                .role(Role.GUEST)
                .build();
    }
}

