package com.rthongpon.url_shortener.controller;

import com.rthongpon.url_shortener.service.ShortUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final ShortUrlService shortUrlService;

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        return ResponseEntity.status(HttpStatus.FOUND).location(shortUrlService.getOriginalUrl(shortCode)).build();
    }
}
