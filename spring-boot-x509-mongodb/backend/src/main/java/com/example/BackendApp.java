package com.example;

import com.example.domain.Task;
import com.example.repository.TaskRepository;
import io.spiffe.provider.SpiffeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class BackendApp implements CommandLineRunner {

    final
    TaskRepository taskRepository;

    public BackendApp(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public static void main(String[] args) {
        // Install Spiffe Provider
        SpiffeProvider.install();
        SpringApplication.run(BackendApp.class, args);
    }

    public void run(String... args) {
        createTasks();
    }

    void createTasks() {
        System.out.println("Creating tasks...");
        taskRepository.save(new Task(1L, "Call my mom", true));
        taskRepository.save(new Task(2L, "Write SPIFFE tutorial", false));
    }
}
