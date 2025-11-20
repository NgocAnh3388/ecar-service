package com.ecar.ecarservice.controller;

import com.ecar.ecarservice.entities.AppUser;
import com.ecar.ecarservice.enums.AppRole;
import com.ecar.ecarservice.repositories.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private final AppUserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;


    public AuthController(AppUserRepository userRepo,
                          BCryptPasswordEncoder passwordEncoder,
                          AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
    }

    // =================== ĐĂNG KÝ TÀI KHOẢN MỚI ===================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Email already exists"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(AppRole.CUSTOMER);
        user.setActive(true);
        AppUser saved = userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", saved.getId(),
                        "email", saved.getEmail(),
                        "name", saved.getFullName(),
                        "roles", saved.getRoles()
                ));
    }

    // =================== ĐĂNG NHẬP ===================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        // Sử dụng AuthenticationManager của Spring Security để xác thực thông tin đăng nhập
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            // Nếu xác thực thành công, lưu thông tin vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AppUser user = userRepo.findByEmail(email).orElseThrow();
            return ResponseEntity.ok(Map.of(
                    "email", user.getEmail(),
                    "name", user.getFullName(),
                    "roles", user.getRoles()
            ));
        } catch (BadCredentialsException ex) {
            // Nếu xác thực thất bại (sai email/mật khẩu), trả về lỗi 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","Invalid email or password"));
        }
    }

    // =================== ĐĂNG XUẤT ===================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // Xóa thông tin xác thực khỏi SecurityContext để đăng xuất người dùng
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message","Logged out successfully"));
    }
}
