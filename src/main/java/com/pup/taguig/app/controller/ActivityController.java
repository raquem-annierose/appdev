package com.pup.taguig.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.model.Activity;
import com.pup.taguig.app.service.ActivityService;
import com.pup.taguig.app.service.impl.ActivityServiceImpl;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService = new ActivityServiceImpl();

    @PostMapping
    public Activity createActivity(@RequestParam String name, @RequestParam(required = false) String description) {
        return activityService.createActivity(name, description);
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }

    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestParam String name, @RequestParam(required = false) String description) {
        return activityService.updateActivity(id, name, description);
    }

    @DeleteMapping("/{id}")
    public boolean deleteActivity(@PathVariable Long id) {
        return activityService.deleteActivity(id);
    }
}
