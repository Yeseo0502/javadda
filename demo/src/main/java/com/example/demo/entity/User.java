package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @Column(name = "username", length = 100, nullable = false)
    private String username;
    
    @Column(name = "password", length = 255, nullable = false)
    private String password;
    
    @Column(name = "enrollment_start_time")
    private Instant enrollmentStartTime; // ⭐ 사용자별 시작 시간 필드
    // 기본 생성자
    public User() {}
    
    // 생성자
    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
    
    // Getter & Setter
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    public Instant getEnrollmentStartTime() { 
        return enrollmentStartTime; 
    }
    public void setEnrollmentStartTime(Instant enrollmentStartTime) { 
        this.enrollmentStartTime = enrollmentStartTime; 
    }
}