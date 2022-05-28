package com.sparta.willbe.user.repository;
import com.sparta.willbe.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //active user
    Optional<User> findByEmailAndIsDeletedFalseAndIsValidTrue(String userEmail);
    //kakao active user
    Optional<User> findByEmailAndProviderAndIsDeletedFalse(String email, String provider);
    //email register but not valid user
    Optional<User> findByEmailAndIsDeletedFalseAndIsValidFalse(String userEmail);
    //user deleted
    Optional<User> findByEmailAndIsDeletedTrue(String userEmail);
}