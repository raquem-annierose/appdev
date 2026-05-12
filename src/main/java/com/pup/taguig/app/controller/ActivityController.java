package com.pup.taguig.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final List<Activity> activities = new ArrayList<>();

    @PostMapping
    public Activity createActivity(@RequestBody ActivityRequest request) {
        Activity activity = new Activity(request.name.trim(), request.description, LocalDateTime.now());
        activities.add(activity);
        return activity;
    }

    @GetMapping
    public List<Activity> getAllActivities() {
        return activities;
    }

    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        for (Activity activity : activities) {
            if (activity.getId().equals(id)) {
                return activity;
            }
        }
        return null;
    }

    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestBody ActivityRequest request) {
        for (Activity activity : activities) {
            if (activity.getId().equals(id)) {
                activity.setName(request.name.trim());
                activity.setDescription(request.description);
                return activity;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public boolean deleteActivity(@PathVariable Long id) {
        return activities.removeIf(activity -> activity.getId().equals(id));
    }

    public static class ActivityRequest {
        public String name;
        public String description;
    }

    public static class Activity {

        private Long id;
        private String name;
        private String description;
        private LocalDateTime createdAt;

        public Activity() {}

        public Activity(String name, String description, LocalDateTime createdAt) {
            this.name = name;
            this.description = description;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
