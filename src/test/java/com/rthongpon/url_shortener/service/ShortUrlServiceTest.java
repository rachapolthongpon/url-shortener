package com.rthongpon.url_shortener.service;

import com.rthongpon.url_shortener.dto.ShortenRequest;
import com.rthongpon.url_shortener.dto.ShortenResponse;
import com.rthongpon.url_shortener.dto.UrlResponse;
import com.rthongpon.url_shortener.exception.NotFoundException;
import com.rthongpon.url_shortener.model.ShortUrl;
import com.rthongpon.url_shortener.repository.ShortUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {
    @Mock
    private ShortUrlRepository urlRepository;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(shortUrlService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void convertToShortUrl_savesUrlAndReturnsShortUrl() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        ShortenRequest request = new ShortenRequest();
        request.setOriginalUrl("https://example.com");
        ShortenResponse response = shortUrlService.convertToShortUrl(request, 1L);
        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(urlRepository).save(captor.capture());
        ShortUrl saved = captor.getValue();
        assertThat(saved.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(saved.getUserId()).isEqualTo(1L);
        assertThat(response.getShortUrl())
                .isEqualTo("http://localhost:8080/r/" + saved.getShortCode());
    }

    @Test
    void getOriginalUrl_whenCodeExists_returnsOriginalUrl() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl("https://example.com");
        when(urlRepository.findByShortCodeAndActiveTrue("a1234")).thenReturn(Optional.of(shortUrl));
        URI originalUrl = shortUrlService.getOriginalUrl("a1234");
        assertThat(originalUrl).isEqualTo(URI.create("https://example.com"));
    }

    @Test
    void getOriginalUrl_whenCodeNotFound_throwsNotFound() {
        when(urlRepository.findByShortCodeAndActiveTrue("a1234")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> shortUrlService.getOriginalUrl("a1234"));
    }

    @Test
    void getAllUserUrl_whenUserHasUrls_returnsListUrlResponse() {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setId(10L);
        shortUrl.setShortCode("abc1234");
        shortUrl.setOriginalUrl("https://example.com");
        when(urlRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(1L)).thenReturn(List.of(shortUrl));
        List<UrlResponse> urlResponse = shortUrlService.getAllUserUrl(1L);
        assertThat(urlResponse).hasSize(1);
        UrlResponse response = urlResponse.get(0);
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getShortCode()).isEqualTo("abc1234");
        assertThat(response.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/r/abc1234");
    }

    @Test
    void getAllUserUrl_whenUserHasNoUrls_returnsEmptyList() {
        when(urlRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        List<UrlResponse> urlResponse = shortUrlService.getAllUserUrl(1L);

        assertThat(urlResponse).isEmpty();
    }

    @Test
    void deactivateUrl_whenUrlBelongsToUser_setsInactiveAndSaves() {
        ShortUrl shortUrl = new ShortUrl();
        when(urlRepository.findByIdAndUserIdAndActiveTrue(10L, 1L)).thenReturn(Optional.of(shortUrl));

        shortUrlService.deactivateUrl(10L, 1L);

        ArgumentCaptor<ShortUrl> captor = ArgumentCaptor.forClass(ShortUrl.class);
        verify(urlRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    void deactivateUrl_whenUrlBelongsToAnotherUser_throwsNotFound() {
        when(urlRepository.findByIdAndUserIdAndActiveTrue(10L , 2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> shortUrlService.deactivateUrl(10L , 2L));

        verify(urlRepository, never()).save(any());
    }
}
