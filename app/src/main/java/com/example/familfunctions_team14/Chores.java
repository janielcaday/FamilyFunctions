package com.example.familfunctions_team14;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Chores {

    public String assignedTo;
    public String description;
    public String id;
    public String imageUrl;
    public String pointsWorth;
    public String status;
    public String title;

    public Chores() {
    }

    public Chores(String assignedTo, String description, String id, String imageUrl, String pointsWorth, String status, String title) {
        this.assignedTo = assignedTo;
        this.description = description;
        this.id = id;
        this.imageUrl = imageUrl;
        this.pointsWorth = pointsWorth;
        this.status = status;
        this.title = title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPointsWorth() {
        return pointsWorth;
    }

    public void setPointsWorth(String pointsWorth) {
        this.pointsWorth = pointsWorth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
