// Enrollment.java
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter @Setter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer enrollmentId;
    
    @Column(nullable = false, length = 50)
    private String userId;
    
    @Column(nullable = false)
    private Integer courseId;
    
    @Column(nullable = false)
    private LocalDateTime enrolledAt;
    
    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
}