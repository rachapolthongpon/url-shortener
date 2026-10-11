package com.rthongpon.url_shortener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UrlResponse {
    private Long id;
    @JsonProperty("short_code")
    private String shortCode;

    @JsonProperty("short_url")
    private String shortUrl;

    @JsonProperty("original_url")
    private String originalUrl;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;
}
