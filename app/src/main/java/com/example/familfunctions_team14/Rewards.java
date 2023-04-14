package com.example.familfunctions_team14;

public class Rewards {

    public String title;
    public String points;
    public String id;
    public String description;

    public Rewards() {
    }

    public Rewards(String title, String points, String id, String description) {
        this.title = title;
        this.points = points;
        this.id = id;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
