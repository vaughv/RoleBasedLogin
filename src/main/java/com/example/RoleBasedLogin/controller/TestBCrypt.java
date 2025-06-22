package com.example.RoleBasedLogin.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        String rawPassword = "hello"; // 🔁 put actual one here
        String hashedPassword = "$2a$12$vIRNv36wOnMvpwlPOKrPIeiiESBjGo.9psigGDGYQuYsJzF4eQd6i"; // 🔁 copy from DB

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(rawPassword, hashedPassword);

        System.out.println("Match? " + matches);
    }
}
