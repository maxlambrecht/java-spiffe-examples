package com.example.domain;

import lombok.Data;
import org.hibernate.annotations.Entity;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@Entity
public class Task {

    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String title;
    private boolean completed;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
