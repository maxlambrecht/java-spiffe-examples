package com.example.controller;

import com.example.service.TasksService;
import com.example.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/tasks")
public class TasksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksController.class);

    @Autowired
    private TasksService tasksService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tasks", tasksService.findAll());
        return "tasks";
    }


    @GetMapping("/new")
    public String newTask(Task task) {
        return "addTask";
    }

    @PostMapping("/new")
    public String createTask(Task task, BindingResult result, Model model) {
        tasksService.save(task);
        model.addAttribute("tasks", tasksService.findAll());
        return "redirect:/tasks";
    }

    @PostMapping(value="/new", params="action=cancel")
    public String cancelAdd(Model model) {
        model.addAttribute("tasks", tasksService.findAll());
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Task task = tasksService.getOne(id);
        model.addAttribute("task", task);
        return "editTask";
    }

    @PostMapping(value="/edit/{id}", params="action=save")
    public String updateTask(@PathVariable("id") Long id, @Validated Task task, BindingResult result, Model model) {
        tasksService.save(task);
        model.addAttribute("tasks", tasksService.findAll());
        return "redirect:/tasks";
    }

    @PostMapping(value="/edit/{id}", params="action=cancel")
    public String cancelEdit(Model model) {
        model.addAttribute("tasks", tasksService.findAll());
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id, Model model) {
        tasksService.deleteById(id);
        model.addAttribute("tasks", tasksService.findAll());
        return "redirect:/tasks";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ModelAndView handleAllExceptions(Exception ex, WebRequest request) {
        LOGGER.error(ex.getMessage());
        ModelAndView view = new ModelAndView();
        view.addObject("error", ex.getMessage());
        view.setViewName("error");
        return view;
    }
}
