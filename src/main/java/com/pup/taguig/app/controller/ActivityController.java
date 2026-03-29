package com.pup.taguig.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.pup.taguig.app.dto.ActivityRequest;
import com.pup.taguig.app.model.Activity;
import com.pup.taguig.app.service.ActivityService;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // GET /activities - Retrieve all activities
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    // GET /activities/{id} - Retrieve a single activity by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activity with ID " + id + " not found.");
        }
        return ResponseEntity.ok(activity);
    }

    // POST /activities - Create a new activity
    @PostMapping
    public ResponseEntity<?> createActivity(@RequestBody ActivityRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }
        Activity created = activityService.createActivity(
                request.getName().trim(),
                request.getDescription()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /activities/{id} - Update an existing activity
    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody ActivityRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name is required.");
        }
        Activity updated = activityService.updateActivity(
                id,
                request.getName().trim(),
                request.getDescription()
        );
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activity with ID " + id + " not found.");
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /activities/{id} - Delete an activity
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        boolean deleted = activityService.deleteActivity(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Activity with ID " + id + " not found.");
        }
        return ResponseEntity.ok("Activity with ID " + id + " deleted successfully.");
    }
}
