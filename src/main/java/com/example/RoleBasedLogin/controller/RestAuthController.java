package com.example.RoleBasedLogin.controller;

import com.example.RoleBasedLogin.entity.*;
import com.example.RoleBasedLogin.payload.AuthRequest;
import com.example.RoleBasedLogin.payload.AuthResponse;
import com.example.RoleBasedLogin.repository.UserRepository;
import com.example.RoleBasedLogin.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.RoleBasedLogin.service.*;

import java.util.HashMap;
import java.util.Map;

import static java.awt.SystemColor.window;

@RestController
@RequestMapping("/auth")
public class RestAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtService jwtService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            System.out.println("🔐 Trying login with email: " + request.getEmail());
            System.out.println("Requested Role: " + request.getRole());
            System.out.println("Password: " + request.getPassword());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
            System.out.println("🔍 Password Match: " + matches);
            System.out.println("User Role in DB: " + user.getRole());

            if (!matches) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            // ✅ Check if user's role matches the role in the request
            if (!user.getRole().name().equalsIgnoreCase(request.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You are not authorized to login as " + request.getRole()));
            }

            System.out.println("🔐 Authenticating...");
            // Authenticate with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            System.out.println("✅ Authentication passed.");

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Decide redirect path based on role
            String redirectPath;
            switch (user.getRole()) {
                case ADMIN -> redirectPath = "/admin/DashBoardAdmin.html";
                case STUDENT -> redirectPath = "/student/DashBoardStudent.html";
                case INSTRUCTOR-> redirectPath = "/teacher/DashBoardTeacher.html";
                default -> redirectPath = "/auth/login.html?error=role";
            }

            // Send token + redirect path to frontend
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("redirect", redirectPath);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String role
     )
    {
        if (role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin registration is not allowed.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(role.toUpperCase()));

        userRepository.save(user);

        return ResponseEntity.ok("Registered successfully.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // remove "Bearer "
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Send only needed fields back
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().toString());



        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/api/user/update")
    public ResponseEntity<?> updateUserDetails(@RequestBody User updatedUser, @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // remove "Bearer "
        String email = jwtService.extractUsername(jwt); // assuming your JWT contains the email

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(user);

        return ResponseEntity.ok("User updated successfully");
    }


}
