package com.example.meetforsport.ui.EventInformation;

import androidx.lifecycle.ViewModel;

public class EventViewModel extends ViewModel  {

    private String sportName;
    private int participantBound;
    private int participants;
    private String dateAndTime;

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

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }
}
