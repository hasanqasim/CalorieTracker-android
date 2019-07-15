package com.example.fitnesstracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class DailySteps {
    @PrimaryKey(autoGenerate = true)
    public int stepsId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "steps")
    public int steps;

    public DailySteps(int userId, String time, int steps) {
        this.userId=userId;
        this.time = time;
        this.steps=steps;
    }

    public int getStepsId(){
        return stepsId;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }
    public void setSteps(String time) {
        this.time = time;
    }

    public int getSteps() {
        return steps;
    }
    public void setSteps(int steps) {
        this.steps = steps;
    }

}
