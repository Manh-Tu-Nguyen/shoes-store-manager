package com.example.backend.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if(username.equals("admin") && password.equals("123")) {
            return ResponseEntity.ok("Login success");
        } else {
            return ResponseEntity.status(401).body("Sai tài khoản");
        }
    }
}