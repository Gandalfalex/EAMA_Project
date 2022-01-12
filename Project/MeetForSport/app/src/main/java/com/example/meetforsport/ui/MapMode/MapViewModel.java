package com.example.meetforsport.ui.MapMode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private Integer minNumOfParticipants;
    private Integer maxNumOfParticipants;
    private int selectedSearchRadius;
    private boolean[] selectedSports;

    public MapViewModel() {
        minNumOfParticipants = null;
        maxNumOfParticipants = null;
    }

    public Integer getMinNumOfParticipants() {
        return minNumOfParticipants;
    }

    public void setMinNumOfParticipants(Integer minNumOfParticipants) {
        this.minNumOfParticipants = minNumOfParticipants;
    }

    public Integer getMaxNumOfParticipants() {
        return maxNumOfParticipants;
    }

    public void setMaxNumOfParticipants(Integer maxNumOfParticipants) {
        this.maxNumOfParticipants = maxNumOfParticipants;
    }

    public int getSelectedSearchRadius() {
        return selectedSearchRadius;
    }

    public void setSelectedSearchRadius(int selectedSearchRadius) {
        this.selectedSearchRadius = selectedSearchRadius;
    }

    public boolean[] getSelectedSports() {
        return selectedSports;
    }

    public void setSelectedSports(boolean[] selectedSports) {
        this.selectedSports = selectedSports;
    }
}