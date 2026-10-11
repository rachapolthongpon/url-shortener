package com.rthongpon.url_shortener.service;

import com.rthongpon.url_shortener.config.JwtProvider;
import com.rthongpon.url_shortener.dto.AuthRequest;
import com.rthongpon.url_shortener.dto.LoginResponse;
import com.rthongpon.url_shortener.exception.BusinessException;
import com.rthongpon.url_shortener.model.User;
import com.rthongpon.url_shortener.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    private AuthRequest request(String email, String password) {
        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private User createUser(Long userId, String email, String passwordHash) {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        return user;
    }

    @Test
    void register_whenEmailIsNew_savesUserWithHashedPassword() {
        when(userRepository.existsByEmail("abc@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("abc1234")).thenReturn("zxy1234");

        AuthRequest request = request("abc@gmail.com", "abc1234");
        authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("abc@gmail.com");
        assertThat(saved.getPasswordHash()).isEqualTo("zxy1234");
        assertThat(saved.getPasswordHash()).isNotEqualTo("password123");
    }

    @Test
    void register_whenEmailAlreadyExists_throwsConflict() {
        when(userRepository.existsByEmail("abc@gmail.com")).thenReturn(true);

        AuthRequest request = request("abc@gmail.com", "abc1234");

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.register(request));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_whenTheInformationIsCorrect_returnsToken() {
        User user = createUser(1L, "abc@gmail.com", "zxy1234");
        when(userRepository.findByEmail("abc@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("abc1234", "zxy1234")).thenReturn(true);
        when(jwtProvider.generateToken(1L, "abc@gmail.com")).thenReturn("token");
        when(jwtProvider.getExpirationSeconds()).thenReturn(3600L);

        AuthRequest request = request("abc@gmail.com", "abc1234");
        LoginResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(user.getLastLoginAt()).isNotNull();
    }

    @Test
    void login_whenPasswordIsWrong_throwsUnauthorized() {
        User user = createUser(1L, "abc@gmail.com", "zxy1234");

        when(userRepository.findByEmail("abc@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("abc1234", "zxy1234")).thenReturn(false);

        AuthRequest request = request("abc@gmail.com", "abc1234");
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtProvider, never()).generateToken(anyLong(), anyString());
    }

    @Test
    void login_whenEmailNotFound_throwsUnauthorized() {
        when(userRepository.findByEmail("abc@gmail.com")).thenReturn(Optional.empty());

        AuthRequest request = request("abc@gmail.com", "abc1234");
        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtProvider, never()).generateToken(anyLong(), anyString());
    }
}
