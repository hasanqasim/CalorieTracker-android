package com.example.fitnesstracker;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduledIntentService extends IntentService {
    private Integer calorieGoal, totalSteps;
    private double totalCaloriesConsumed, totalCaloriesBurned;
    private List<Integer> list;
    DailyStepsDatabase db = null;

    public ScheduledIntentService() {
        super("ScheduledIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //counter++;
        db = Room.databaseBuilder(getApplicationContext(),
                DailyStepsDatabase.class, "DailyStepsDatabase")
                .fallbackToDestructiveMigration()
                .build();
        list = db.DailyStepsDao().findAllID();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                SharedPreferences sharedPrefCalorieGoal = getSharedPreferences(Integer.toString((list.get(i))), Context.MODE_PRIVATE);
                calorieGoal = sharedPrefCalorieGoal.getInt("calorieGoal", 0);
                SharedPreferences sharedPrefTotalSteps = getSharedPreferences(Integer.toString((list.get(i))), Context.MODE_PRIVATE);
                totalSteps = sharedPrefTotalSteps.getInt("totalSteps", 0);
                SharedPreferences sharedPrefCaloriesConsumed = getSharedPreferences(Integer.toString((list.get(i))), Context.MODE_PRIVATE);
                String sc = sharedPrefCaloriesConsumed.getString("caloriesConsumed", null);
                totalCaloriesConsumed = Double.parseDouble(sc);
                SharedPreferences sharedPrefCaloriesBurned = getSharedPreferences(Integer.toString((list.get(i))), Context.MODE_PRIVATE);
                String sb = sharedPrefCaloriesBurned.getString("caloriesBurned", null);
                totalCaloriesBurned = Double.parseDouble(sb);
                GetUserAsyncTask task = new GetUserAsyncTask();
                try {
                    String user = task.execute(list.get(i)).get();
                    JSONObject userObj = new JSONObject(user);
                    Integer userId = userObj.getInt("userId");
                    String firstName = userObj.getString("firstName");
                    String surname = userObj.getString("surname");
                    String email = userObj.getString("email");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date dob = sdf.parse(userObj.getString("dob"));
                    Date reportDate = sdf.parse(sdf.format(new Date()));
                    Double height = userObj.getDouble("height");
                    Double weight = userObj.getDouble("weight");
                    String gender = userObj.getString("gender");
                    String address = userObj.getString("address");
                    String postcode = userObj.getString("postcode");
                    int levelOfActivity = userObj.getInt("levelOfActivity");
                    int stepsPerMile = userObj.getInt("stepsPerMile");
                    Users uID = new Users(userId,firstName,surname,email, dob, height, weight, gender,address,postcode,levelOfActivity,stepsPerMile);
                    Report reportEntry = new Report(null, uID, reportDate, totalCaloriesConsumed, totalCaloriesBurned, totalSteps, calorieGoal);
                    PostReportEntryAsyncTask postReportEntry = new PostReportEntryAsyncTask();
                    postReportEntry.execute(reportEntry);
                }catch(Exception e){
                }
            }
        }
        //after successful posting
        db.DailyStepsDao().deleteAll();
        for (int i = 0; i < list.size(); i++){
            getApplicationContext().getSharedPreferences(Integer.toString(list.get(i)), 0).edit().clear().apply();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    private class GetUserAsyncTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            return RestClient.findByUserID(params[0]);
        }
    }

    private class  PostReportEntryAsyncTask extends AsyncTask<Report, Void, Void> {
        @Override
        protected Void doInBackground(Report... params) {
            RestClient.postReportEntry(params[0]);
            return null;
        }
    }
}
