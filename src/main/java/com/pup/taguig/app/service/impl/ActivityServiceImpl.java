package com.pup.taguig.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pup.taguig.app.dto.ActivityRequestDTO;
import com.pup.taguig.app.dto.ActivityResponseDTO;
import com.pup.taguig.app.model.Activity;
import com.pup.taguig.app.repository.ActivityRepository;
import com.pup.taguig.app.service.ActivityService;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Long createActivity(ActivityRequestDTO request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        
        Activity activity = new Activity(
                request.getName().trim(),
                request.getDescription(),
                LocalDateTime.now()
        );
        
        activity = activityRepository.save(activity);
        return activity.getId();
    }

    @Override
    public List<ActivityResponseDTO> getAllActivities() {
        List<Activity> activities = activityRepository.findAll();
        List<ActivityResponseDTO> response = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityResponseDTO dto = new ActivityResponseDTO(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getCreatedAt()
            );
            response.add(dto);
        }
        return response;
    }

    @Override
    public ActivityResponseDTO getActivityById(Long id) {
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            return new ActivityResponseDTO(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getCreatedAt()
            );
        }
        return null;
    }

    @Override
    public ActivityResponseDTO updateActivity(Long id, ActivityRequestDTO request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        
        Activity activity = activityRepository.findById(id).orElse(null);
        if (activity != null) {
            activity.setName(request.getName().trim());
            activity.setDescription(request.getDescription());
            activity = activityRepository.save(activity);
            return new ActivityResponseDTO(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getCreatedAt()
            );
        }
        return null;
    }

    @Override
    public boolean deleteActivity(Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
