package com.rthongpon.url_shortener.repository;

import com.rthongpon.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    boolean existsByShortCode(String shortCode);
    Optional<ShortUrl> findByShortCodeAndActiveTrue(String shortCode);
    List<ShortUrl> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Long userId);
    Optional<ShortUrl> findByIdAndUserIdAndActiveTrue(Long id, Long userId);
}
