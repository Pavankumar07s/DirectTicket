package com.Directtickets.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "user_information")
public class UserInformation {

    @Id
    private String id;

    private String name;

    private String email;

    private String image;

    private List<String> upcomingTravelPlans;

    private List<String> pastTravelEvents;

    private LocalDate memberSince;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getUpcomingTravelPlans() {
        return upcomingTravelPlans;
    }

    public void setUpcomingTravelPlans(List<String> upcomingTravelPlans) {
        this.upcomingTravelPlans = upcomingTravelPlans;
    }

    public List<String> getPastTravelEvents() {
        return pastTravelEvents;
    }

    public void setPastTravelEvents(List<String> pastTravelEvents) {
        this.pastTravelEvents = pastTravelEvents;
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }
}

