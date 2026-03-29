package com.pup.taguig.app.service;

import java.util.List;

import com.pup.taguig.app.model.Activity;

public interface ActivityService {

    Activity createActivity(String name, String description);

    List<Activity> getAllActivities();

    Activity getActivityById(Long id);

    Activity updateActivity(Long id, String name, String description);

    boolean deleteActivity(Long id);
}
