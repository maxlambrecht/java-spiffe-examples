package com.example.repository;

import com.example.domain.Task;
import java.util.List;

//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TaskRepository extends MongoRepository<Task, Long> {
}
