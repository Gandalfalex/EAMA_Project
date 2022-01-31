package com.example.meetforsport.ui.EventMode;

import androidx.lifecycle.ViewModel;

public class EventFragmentViewModel extends ViewModel {

    private Integer minNumOfParticipants;
    private Integer maxNumOfParticipants;
    private int selectedOrderOption;
    private int selectedSearchRadius;
    private boolean[] selectedSports;

    public EventFragmentViewModel() {
        minNumOfParticipants = null;
        maxNumOfParticipants = null;
        setSelectedSearchRadius(5);
        setSelectedOrderOption(1);
        setSelectedSports(new boolean[]{true, true, true, true, true});
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

    public int getSelectedOrderOption() {
        return selectedOrderOption;
    }

    public void setSelectedOrderOption(int selectedOrderOption) {
        this.selectedOrderOption = selectedOrderOption;
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