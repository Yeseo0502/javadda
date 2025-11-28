package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 회원가입public User registerUser(User user) {return userRepository.save(user);}

    public void registerUser(User user) {
        userRepository.save(user);
    }

    // 로그인
    public User login(String userId, String password) {
        return userRepository.findByUserIdAndPassword(userId, password).orElse(null);
    }
    
    // 아이디 중복 체크
    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }
    
    // 사용자 찾기
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }
}