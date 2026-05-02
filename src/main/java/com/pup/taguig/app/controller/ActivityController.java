package com.pup.taguig.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pup.taguig.app.dto.ActivityRequestDTO;
import com.pup.taguig.app.dto.ActivityResponseDTO;
import com.pup.taguig.app.service.ActivityService;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public Long createActivity(@RequestBody ActivityRequestDTO request) {
        Long result = null;
        if (request != null && request.getName() != null && !request.getName().trim().isEmpty()) {
            result = activityService.createActivity(request);
        }
        return result;
    }

    @GetMapping
    public List<ActivityResponseDTO> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    
    public ActivityResponseDTO getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id);
    }

    @PutMapping("/{id}")
    public ActivityResponseDTO updateActivity(@PathVariable Long id, @RequestBody ActivityRequestDTO request) {
        ActivityResponseDTO result = null;
        if (request != null && request.getName() != null && !request.getName().trim().isEmpty()) {
            result = activityService.updateActivity(id, request);
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public String deleteActivity(@PathVariable Long id) {
        boolean deleted = activityService.deleteActivity(id);
        if (deleted) {
            return "Activity with ID " + id + " deleted successfully.";
        }
        return "Activity with ID " + id + " not found";
    }
}
