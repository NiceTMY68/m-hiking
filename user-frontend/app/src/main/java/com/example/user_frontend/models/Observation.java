package com.example.user_frontend.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Observation implements Serializable {
    private String id;
    private String hikeId;
    private String observation;
    private Date time;
    private String comments;
    private String category;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;

    public Observation() {
        this.id = UUID.randomUUID().toString();
        this.time = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Observation(String hikeId, String observation, Date time) {
        this();
        this.hikeId = hikeId;
        this.observation = observation;
        if (time != null) {
            this.time = time;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHikeId() {
        return hikeId;
    }

    public void setHikeId(String hikeId) {
        this.hikeId = hikeId;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
        this.updatedAt = new Date();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
        this.updatedAt = new Date();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
        this.updatedAt = new Date();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = new Date();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "id='" + id + '\'' +
                ", hikeId='" + hikeId + '\'' +
                ", observation='" + observation + '\'' +
                ", time=" + time +
                ", category='" + category + '\'' +
                '}';
    }
}

