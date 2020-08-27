package com.example.model;

import lombok.Data;

@Data
public class Task {
    private long id;
    private String title;
    private boolean completed;
}
