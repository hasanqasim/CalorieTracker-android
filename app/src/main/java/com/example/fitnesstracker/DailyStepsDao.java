package com.example.fitnesstracker;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DailyStepsDao {

    @Query("SELECT * FROM dailysteps")
    List<DailySteps> getAll();


    @Query("SELECT * FROM dailysteps WHERE user_id = :userId")
    List<DailySteps> findByID(int userId);

    @Query("SELECT DISTINCT user_id FROM dailysteps")
    List<Integer> findAllID();

    @Insert
    long insert(DailySteps steps);

    @Query("DELETE FROM dailysteps")
    void deleteAll();

    @Query("SELECT * FROM dailysteps WHERE time LIKE :t LIMIT 1")
    DailySteps findByTime(String t);

    @Query("SELECT SUM(steps) FROM dailysteps WHERE user_id = :userId")
    int stepsSum(int userId);

    @Update(onConflict = REPLACE)
    public void updateStepsEntry(DailySteps... steps);


}
