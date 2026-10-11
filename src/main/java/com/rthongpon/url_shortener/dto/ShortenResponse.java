package com.rthongpon.url_shortener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShortenResponse {
    @JsonProperty("short_url")
    private String shortUrl;
}
