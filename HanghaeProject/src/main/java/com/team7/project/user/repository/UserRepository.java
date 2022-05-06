package com.team7.project.user.repository;

import com.team7.project.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String userEmail);
    Optional<User> findByNickname(String userName);
}