package com.example.fitnesstracker;

import java.util.Date;

public class Report {
    private Integer reportId;
    private Users userId;
    private Date reportDate;
    private double totalCaloriesConsumed;
    private double totalCaloriesBurned;
    private int totalStepsTaken;
    private int calorieGoal;



    public Report(Integer reportId, Users userId, Date reportDate, double totalCaloriesConsumed, double totalCaloriesBurned, int totalStepsTaken, int calorieGoal) {
        this.reportId = reportId;
        this.userId = userId;
        this.reportDate = reportDate;
        this.totalCaloriesConsumed = totalCaloriesConsumed;
        this.totalCaloriesBurned = totalCaloriesBurned;
        this.totalStepsTaken = totalStepsTaken;
        this.calorieGoal = calorieGoal;
    }
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public double getTotalCaloriesConsumed() {
        return totalCaloriesConsumed;
    }

    public void setTotalCaloriesConsumed(double totalCaloriesConsumed) {
        this.totalCaloriesConsumed = totalCaloriesConsumed;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(double totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public int getTotalStepsTaken() {
        return totalStepsTaken;
    }

    public void setTotalStepsTaken(int totalStepsTaken) {
        this.totalStepsTaken = totalStepsTaken;
    }

    public int getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(int calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }
}
