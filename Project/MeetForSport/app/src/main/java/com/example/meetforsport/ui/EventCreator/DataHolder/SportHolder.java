package com.example.meetforsport.ui.EventCreator.DataHolder;

public class SportHolder extends DataHolder{

    private int minPlayer = 0;
    private int maxPlayer = 0;

    public SportHolder(int id, int minPlayer, int maxPlayer, String s_name){
        super(id, s_name);
        this.maxPlayer = maxPlayer;
        this.minPlayer = minPlayer;
    }

    public SportHolder(int id, String name){
        super(id, name);
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

}
