package com.rthongpon.url_shortener.service;

import com.rthongpon.url_shortener.config.JwtProvider;
import com.rthongpon.url_shortener.dto.AuthRequest;
import com.rthongpon.url_shortener.dto.LoginResponse;
import com.rthongpon.url_shortener.exception.BusinessException;
import com.rthongpon.url_shortener.model.User;
import com.rthongpon.url_shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void register(AuthRequest request) {
        String email = request.getEmail().toLowerCase();
        if(userRepository.existsByEmail(email))
            throw new BusinessException(HttpStatus.CONFLICT, "Email already registered");
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(AuthRequest request) {
        String email = request.getEmail().toLowerCase();
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.getPassword() , u.getPasswordHash()))
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        String token = jwtProvider.generateToken(user.getId(), user.getEmail());
        return new LoginResponse(token, "Bearer", jwtProvider.getExpirationSeconds());
    }
}
