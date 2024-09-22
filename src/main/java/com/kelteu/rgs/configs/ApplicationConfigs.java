package com.kelteu.rgs.configs;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "krgs")
public @Data class ApplicationConfigs {
    Double reverseCountryMaxDistanceThresholdInDegrees;
    List<String> openApiServers;
}
