package com.example.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Task {

    @Id
    private Long id;
    private String title;
    private boolean completed;

    public Task(Long id, String title, boolean completed) {
        super();
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

//    public Long getId() {
//        return id;
//    }
//    public void setId(Long id) {
//        this.id = id;
//    }
//    public String getTitle() {
//        return title;
//    }
//    public void setTitle(String title) {
//        this.title = title;
//    }
//    public boolean isCompleted() {
//        return completed;
//    }
//    public void setCompleted(boolean completed) {
//        this.completed = completed;
//    }
}
