package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // ⭐ JpaRepository가 userId를 이용해 User를 자동으로 찾아주는 메서드
    //User findByUserId(String userId); 
    //Spring Boot에서 JPA 쓰는 가장 표준적인 방식

    // userId로 사용자 찾기
    Optional<User> findByUserId(String userId);
    
    // userId와 password로 사용자 찾기 (로그인용)
    Optional<User> findByUserIdAndPassword(String userId, String password);
    
    // userId가 존재하는지 확인
    boolean existsByUserId(String userId);
}