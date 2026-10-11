package com.rthongpon.url_shortener.service;

import com.rthongpon.url_shortener.dto.ShortenRequest;
import com.rthongpon.url_shortener.dto.ShortenResponse;
import com.rthongpon.url_shortener.dto.UrlResponse;
import com.rthongpon.url_shortener.exception.NotFoundException;
import com.rthongpon.url_shortener.model.ShortUrl;
import com.rthongpon.url_shortener.repository.ShortUrlRepository;
import com.rthongpon.url_shortener.util.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortUrlService {
    @Value("${app.base-url}")
    private String baseUrl;

    private final ShortUrlRepository urlRepository;

    @Transactional(readOnly = true)
    public URI getOriginalUrl(String shortCode) {
        String originalUrl = urlRepository.findByShortCodeAndActiveTrue(shortCode)
                .map(ShortUrl::getOriginalUrl)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));
        return URI.create(originalUrl);
    }

    @Transactional
    public ShortenResponse convertToShortUrl(ShortenRequest request, Long userId) {
        log.info("ShortUrlService.convertToShortUrl : start convertToShortUrl");
        String shortCode = createShortCode();
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortCode(shortCode);
        shortUrl.setOriginalUrl(request.getOriginalUrl());
        shortUrl.setUserId(userId);
        log.info("ShortUrlService.convertToShortUrl : Created short URL code={} userId={}", shortCode, userId);
        urlRepository.save(shortUrl);

        String shortUrlString = baseUrl + "/r/" + shortUrl.getShortCode();
        ShortenResponse response = new ShortenResponse();
        response.setShortUrl(shortUrlString);

        log.info("ShortUrlService.convertToShortUrl : end convertToShortUrl");
        return response;
    }

    @Transactional(readOnly = true)
    public List<UrlResponse> getAllUserUrl(Long userId) {
        return urlRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId).stream()
                .map(url -> new UrlResponse(
                        url.getId(),
                        url.getShortCode(),
                        baseUrl + "/r/" + url.getShortCode(),
                        url.getOriginalUrl(),
                        url.getCreatedAt(),
                        url.getUpdatedAt()
                )).toList();
    }

    @Transactional
    public void deactivateUrl(Long id, Long userId) {
        ShortUrl url = urlRepository.findByIdAndUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));
        url.setActive(false);
        urlRepository.save(url);
    }

    private String createShortCode() {
        for (int i = 0; i < 5 ; i++) {
            String code = Base62.randomCode();
            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Can't create short url");
    }

}
