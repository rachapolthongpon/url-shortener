package com.rthongpon.url_shortener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ShortenRequest {
    @JsonProperty("original_url")
    @NotBlank(message = "is required")
    @Size(max = 2048, message = "must be at most 2048 characters")
    @URL(regexp = "^https?://.*", message = "must be a valid http(s) URL")
    private String originalUrl;
}
