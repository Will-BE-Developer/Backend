package com.team7.project.user.model;

import com.team7.project._timestamped.model.Timestamped;
import com.team7.project.comments.model.Comment;
import com.team7.project.interview.model.Interview;
import com.team7.project.scrap.model.Scrap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity
public class User extends Timestamped implements UserDetails {

    // ID가 자동으로 생성 및 증가합니다.
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isValid;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String githubLink;

    @Column(nullable = true)
    private String introduce;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = true)
    private String token;

    @Column(nullable = false)
    private String isDeleted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Fetch(FetchMode.JOIN)
    List<Interview> interviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Fetch(FetchMode.JOIN)
    List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Fetch(FetchMode.JOIN)
    List<Comment> comments = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
