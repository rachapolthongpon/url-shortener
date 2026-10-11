package com.rthongpon.url_shortener.controller;

import com.rthongpon.url_shortener.dto.AuthRequest;
import com.rthongpon.url_shortener.dto.LoginResponse;
import com.rthongpon.url_shortener.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
