package com.example.mobilegameappdevproject;

public class LeaderboardModel {
    String txtRank, txtRankName, txtRankScore;

    public LeaderboardModel(String txtRank, String txtRankName, String txtRankScore) {
        this.txtRank = txtRank;
        this.txtRankName = txtRankName;
        this.txtRankScore = txtRankScore;
    }

    public String getTxtRank() {
        return txtRank;
    }

    public String getTxtRankName() {
        return txtRankName;
    }

    public String getTxtRankScore() {
        return txtRankScore;
    }

    public void setTxtRank(String txtRank) {
        this.txtRank = txtRank;
    }
}
