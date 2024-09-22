package com.kelteu.rgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication(exclude = { ErrorMvcAutoConfiguration.class })
public class Application {
    @PostConstruct
    public void setup() {
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("====================STARTED KELTEU RGS====================");
    }
}
