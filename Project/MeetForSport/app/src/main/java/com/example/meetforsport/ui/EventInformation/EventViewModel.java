package com.example.meetforsport.ui.EventInformation;

import android.location.Location;

import androidx.lifecycle.ViewModel;

public class EventViewModel extends ViewModel  {

    private String sportName;
    private int participantBound;
    private int numberOfParticipants;
    private String date;
    private String time;
    private String description;
    private Location location;

    public EventViewModel() {

    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public int getParticipantBound() {
        return participantBound;
    }

    public void setParticipantBound(int participantBound) {
        this.participantBound = participantBound;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
