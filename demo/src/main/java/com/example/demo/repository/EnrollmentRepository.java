// EnrollmentRepository.java
package com.example.demo.repository;

import com.example.demo.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByUserIdAndCourseId(String userId, Integer courseId);
    List<Enrollment> findByUserId(String userId);  // 이 메서드 추가!
}