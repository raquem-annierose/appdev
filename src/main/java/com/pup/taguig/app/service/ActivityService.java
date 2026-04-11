package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.model.Activity;

public interface ActivityService {

    public Activity createActivity(String name, String description);
    public List<Activity> getAllActivities();
    public Activity getActivityById(Long id);
    public Activity updateActivity(Long id, String name, String description);
    public boolean deleteActivity(Long id);

}
