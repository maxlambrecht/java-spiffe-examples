package com.example;

import io.spiffe.provider.SpiffeProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApp {
    public static void main(String[] args) {

        // Install Spiffe Provider
        SpiffeProvider.install();

        SpringApplication.run(BackendApp.class, args);
    }
}