// EnrollmentService.java
package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    
    @Transactional
    public boolean enrollCourse(String userId, String courseName) {
        // 강의 찾기
        Course course = courseRepository.findByCourseName(courseName)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 강의입니다."));
        
        // 이미 수강신청했는지 확인
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, course.getCourseId())) {
            return false; // 이미 신청함
        }
        
        // 수강신청 저장
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(course.getCourseId());
        enrollmentRepository.save(enrollment);
        
        return true;
    }
    
    // 이 메서드 추가!
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyEnrollments(String userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        
        return enrollments.stream().map(enrollment -> {
            Course course = courseRepository.findById(enrollment.getCourseId())
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
            
            Map<String, Object> result = new HashMap<>();
            result.put("enrollmentId", enrollment.getEnrollmentId());
            result.put("courseName", course.getCourseName());
            result.put("enrolledAt", enrollment.getEnrolledAt().toString());
            
            return result;
        }).collect(Collectors.toList());
    }
}