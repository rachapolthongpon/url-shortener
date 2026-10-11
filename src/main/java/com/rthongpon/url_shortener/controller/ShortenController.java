package com.rthongpon.url_shortener.controller;

import com.rthongpon.url_shortener.dto.ShortenRequest;
import com.rthongpon.url_shortener.dto.ShortenResponse;
import com.rthongpon.url_shortener.dto.UrlResponse;
import com.rthongpon.url_shortener.service.ShortUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShortenController {
    private final ShortUrlService shortUrlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> createShortUrl(@Valid @RequestBody ShortenRequest request, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlService.convertToShortUrl(request, userId));
    }

    @GetMapping("/urls")
    public List<UrlResponse> getMyUrls(@AuthenticationPrincipal Long userId) {
        return shortUrlService.getAllUserUrl(userId);
    }

    @DeleteMapping("/urls/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id,
                                          @AuthenticationPrincipal Long userId) {
        shortUrlService.deactivateUrl(id, userId);
        return ResponseEntity.noContent().build();
    }
}
