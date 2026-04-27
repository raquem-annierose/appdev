package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.dto.ActivityRequestDTO;
import com.pup.taguig.app.dto.ActivityResponseDTO;

public interface ActivityService {

    public Long createActivity(ActivityRequestDTO activity);
    public List<ActivityResponseDTO> getAllActivities();
    public ActivityResponseDTO getActivityById(Long id);
    public ActivityResponseDTO updateActivity(Long id, ActivityRequestDTO activity);
    public boolean deleteActivity(Long id);

}
