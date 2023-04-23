package com.example.service;

import com.example.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.lang.String.format;

@Service
public class TasksService {

    @Value("${tasks.service}")
    String tasksUrl;

    @Autowired
    RestOperations restOperationsTls;

    RestOperations restOperations = new RestTemplate();

    public List<Task> findAll() {
        ResponseEntity<List<Task>> tasks = restOperationsTls.exchange(tasksUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Task>>(){});
        return tasks.getBody();
    }

    public Task getOne(Long id) {
        String url = tasksUrl + "/%s";
        return restOperationsTls.getForObject(format(url, id), Task.class);
    }

    public Task save(Task task) {
        return restOperationsTls.postForObject(tasksUrl, task, Task.class);
    }

    public void deleteById(Long id) {
        String url = tasksUrl + "/%s";
        restOperationsTls.delete(format(url, id));
    }
}
