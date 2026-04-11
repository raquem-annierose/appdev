package com.pup.taguig.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createActivity(@RequestBody ActivityRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }
        Activity activity = activityService.createActivity(request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activity with ID " + id + " not found.");
        }
        return ResponseEntity.ok(activity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody ActivityRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }
        Activity activity = activityService.updateActivity(id, request.getName(), request.getDescription());
        if (activity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activity with ID " + id + " not found.");
        }
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        boolean removed = activityService.deleteActivity(id);
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activity with ID " + id + " not found.");
        }
        return ResponseEntity.ok("Activity with ID " + id + " deleted successfully.");
    }
}
