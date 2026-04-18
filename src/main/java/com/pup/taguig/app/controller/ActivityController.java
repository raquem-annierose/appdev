package com.pup.taguig.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.model.Activity;
import com.pup.taguig.app.service.ActivityService;
import com.pup.taguig.app.service.impl.ActivityServiceImpl;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService = new ActivityServiceImpl();

    static class ActivityRequest {
        private String name;
        private String description;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    @PostMapping
    public Activity createActivity(@RequestBody ActivityRequest request) {
        return activityService.createActivity(request.getName(), request.getDescription());
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
    public Activity updateActivity(@PathVariable Long id, @RequestBody ActivityRequest request) {
        return activityService.updateActivity(id, request.getName(), request.getDescription());
    }

    @DeleteMapping("/{id}")
    public boolean deleteActivity(@PathVariable Long id) {
        return activityService.deleteActivity(id);
    }
}
