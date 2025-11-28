package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.ui.Model;
import com.example.demo.service.EnrollmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.List;
import java.time.Instant;
import java.net.URLEncoder; 
import java.nio.charset.StandardCharsets; 
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    @Autowired
    private UserService userService; // 유저 관련 DB 작업 담당하는 서비스
    private final EnrollmentService enrollmentService; // 수강/기록 관련 서비스

    @Autowired 
    private PasswordEncoder passwordEncoder; // 비밀번호 암호화용

    @GetMapping("/")
    public String home() {
        return "index"; // 메인 페이지 보여주는 곳
    }

    @GetMapping("/index")
    public String index() {
        return "index"; // 메인 페이지
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // 로그인 화면
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello"; // 회원가입 화면
    }

    // 회원가입 처리
    @PostMapping("/hello")
    public String registerUser(@RequestParam String userId, @RequestParam String username, @RequestParam String password, Model model) {
        try {
            // 아이디 중복 체크 — 이미 있으면 회원가입 막아버림
            if (userService.isUserIdExists(userId)) {
                model.addAttribute("error", "이미 존재하는 아이디입니다!");
                return "hello";
            }

            // 비번 암호화 (안 하면 절대 안 됨!)
            String encodedPassword = passwordEncoder.encode(password);

            // 새 유저 엔티티 만들어서 값 채우기
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            user.setPassword(encodedPassword);

            // DB에 저장!
            userService.registerUser(user);

            model.addAttribute("success", "회원가입 성공! 로그인해주세요 😊");
            return "login";
        } catch (Exception e) {
            // 뭔가 잘못됐을 때 에러 메시지 뿌려줌
            model.addAttribute("error", "회원가입 실패: " + e.getMessage());
            return "hello";
        }
    }

    // 로그인 처리
    @PostMapping("/login")
    public String loginUser(@RequestParam String userId, @RequestParam String password, Model model, HttpSession session) {
        try {
            // 입력한 아이디로 유저 조회 — 없으면 null 나옴
            User user = userService.findByUserId(userId);

            // 여기서 비번도 같이 검사! 둘 다 통과해야 로그인 시켜줌
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {

                // 첫 로그인이라면 타이머 시작 시간 박아주기
                if (user.getEnrollmentStartTime() == null) {
                    user.setEnrollmentStartTime(Instant.now());
                    userService.registerUser(user);
                }

                // 로그인 성공 → 세션에 정보 넣어버림
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());

                // URL에 넣으려고 인코딩 작업
                String encodedUserId = URLEncoder.encode(user.getUserId(), StandardCharsets.UTF_8.toString());

                // 로그인 성공하면 /test 페이지로 이동!
                return "redirect:/test?userId=" + encodedUserId;
            }

            // 로그인 실패 시 메시지 출력
            model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다!");
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", "로그인 실패: " + e.getMessage());
            return "login";
        }
    }

    // 유저 시간 정보 세팅하는 공통 메서드
    private boolean addUserTimeInfo(String userId, Model model) {
        // DB에서 유저 다시 가져오기
        User user = userService.findByUserId(userId);

        // 유저 자체가 없으면 잘못된 접근 → false 리턴해서 막아버림
        if (user == null) {
            return false;
        }

        // 화면에서 사용할 기본 유저 정보 넣기
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("username", user.getUsername());

        // 타이머 시작 시간이 비어있다면 여기서 생성해주고 저장
        if (user.getEnrollmentStartTime() == null) {
            Instant now = Instant.now();
            user.setEnrollmentStartTime(now);
            userService.registerUser(user);

            model.addAttribute("currentTime", now.toEpochMilli());
            model.addAttribute("startTime", now.toEpochMilli());
        } else {
            long currentTimeMillis = Instant.now().toEpochMilli();
            long startTimeMillis = user.getEnrollmentStartTime().toEpochMilli();

            model.addAttribute("currentTime", currentTimeMillis);
            model.addAttribute("startTime", startTimeMillis);
        }

        return true;
    }

    @GetMapping("/test")
    public String test(@RequestParam String userId, Model model) {
        // 유저 정보 세팅 실패하면 로그인부터 다시 하게 돌려보냄
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "test";
    }

    @GetMapping("/select")
    public String select(@RequestParam String userId, Model model) {
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "select";
    }

    @GetMapping("/check")
    public String check(@RequestParam String userId, Model model) {
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "check";
    }

    @GetMapping("/celebrate")
    public String celebrate(@RequestParam String userId, Model model) {
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "celebrate";
    }

    @GetMapping("/clear")
    public String clear(@RequestParam String userId, Model model) {
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "clear";
    }

    @GetMapping("/mecro")
    public String mecro(@RequestParam String userId, Model model) {
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "mecro";
    }

    @GetMapping("/networkError")
    public String networkError(@RequestParam String userId, Model model) {
        if (!addUserTimeInfo(userId, model)) {
            return "redirect:/login";
        }
        return "networkError";
    }

    // 수강 등록 API
    @PostMapping("/api/enroll")
    @ResponseBody
    public ResponseEntity<?> enrollCourse(@RequestBody Map<String, String> request, HttpSession session) {
        String userId = (String) session.getAttribute("userId");

        // 로그인 안 한 상태면 막기
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        // 요청에서 강의 이름 가져오기
        String courseName = request.get("courseName");
        boolean success = enrollmentService.enrollCourse(userId, courseName);

        // 등록 성공 여부 반환
        return ResponseEntity.ok(Map.of("success", success));
    }

    // 리스트 화면으로 이동하기 (내 기록 보기)
    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        // 세션에서 아이디 꺼내오기
        String userId = (String) session.getAttribute("userId");

        // 로그인 안 했으면 튕겨냄
        if (userId == null) {
            return "redirect:/login";
        }

        // 유저 이름도 가져와서 화면에 표시
        String username = (String) session.getAttribute("username");
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);

        return "list";
    }

    // 내 수강 목록 API
    @GetMapping("/api/my-enrollments")
    @ResponseBody
    public ResponseEntity<?> getMyEnrollments(HttpSession session) {
        String userId = (String) session.getAttribute("userId");

        // 로그인이 필요한 요청임
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "로그인이 필요합니다."
            ));
        }

        try {
            // 해당 유저의 수강 기록 가져오기
            List<Map<String, Object>> enrollments = enrollmentService.getMyEnrollments(userId);
            String username = (String) session.getAttribute("username");

            return ResponseEntity.ok(Map.of(
                "success", true,
                "username", username != null ? username : userId,
                "enrollments", enrollments
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }

    // 타이머 초기화 API
    @PostMapping("/api/reset-timer")
    @ResponseBody
    public ResponseEntity<String> resetTimer(@RequestParam String userId) {
        try {
            // 유저 정보 가져오기
            User user = userService.findByUserId(userId);

            // 유저가 없으면 초기화 불가능
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // 타이머 시작 시간 비워버리기 → 다음 로그인에서 새로 시작됨
            user.setEnrollmentStartTime(null);
            userService.registerUser(user);

            return ResponseEntity.ok("Timer reset successfully for user: " + userId);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset timer: " + e.getMessage());
        }
    }
}
