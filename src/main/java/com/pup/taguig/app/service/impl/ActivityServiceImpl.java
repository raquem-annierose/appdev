package com.pup.taguig.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pup.taguig.app.model.Activity;
import com.pup.taguig.app.service.ActivityService;

public class ActivityServiceImpl implements ActivityService {

    private final List<Activity> activities = new ArrayList<>();

    @Override
    public Activity createActivity(String name, String description) {
        Activity activity = new Activity(
                name.trim(),
                description,
                LocalDateTime.now()
        );
        activities.add(activity);
        return activity;
    }

    @Override
    public List<Activity> getAllActivities() {
        return activities;
    }

    @Override
    public Activity getActivityById(Long id) {
        for (Activity activity : activities) {
            if (activity.getId().equals(id)) {
                return activity;
            }
        }
        return null;
    }

    @Override
    public Activity updateActivity(Long id, String name, String description) {
        for (Activity activity : activities) {
            if (activity.getId().equals(id)) {
                activity.setName(name.trim());
                activity.setDescription(description);
                return activity;
            }
        }
        return null;
    }

    @Override
    public boolean deleteActivity(Long id) {
        return activities.removeIf(activity -> activity.getId().equals(id));
    }

}
