package com.example.fitnesstracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalorieTrackerFragment extends Fragment {
    private View vCalorieTracker;
    private TextView tvGoal, tvSteps, tvConsumed, tvBurned;
    private Button btnGoal, btnSteps, btnConsumed, btnBurned, btnDeleteAll;
    private Integer calorieGoal, totalSteps;
    private double totalCaloriesConsumed, totalCaloriesBurned;
    private DailyStepsDatabase db = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vCalorieTracker = inflater.inflate(R.layout.fragment_calorie_tracker, container, false);
        db = Room.databaseBuilder(getContext().getApplicationContext(),
                DailyStepsDatabase.class, "DailyStepsDatabase")
                .fallbackToDestructiveMigration()
                .build();
        tvGoal = vCalorieTracker.findViewById(R.id.tv_calorieGoal);
        btnGoal = vCalorieTracker.findViewById(R.id.btn_calorie_goal);
        tvSteps = vCalorieTracker.findViewById(R.id.tv_stepsTaken);
        btnSteps = vCalorieTracker.findViewById(R.id.btn_steps_taken);
        tvConsumed = vCalorieTracker.findViewById(R.id.tv_caloriesConsumed);
        btnConsumed = vCalorieTracker.findViewById(R.id.btn_calories_consumed);
        tvBurned = vCalorieTracker.findViewById(R.id.tv_caloriesBurned);
        btnBurned = vCalorieTracker.findViewById(R.id.btn_calories_burned);
        btnDeleteAll = vCalorieTracker.findViewById(R.id.btn_delete_all);

        GetTotalStepsAsyncTask task = new GetTotalStepsAsyncTask();
        try {
            totalSteps = task.execute().get();
        } catch (Exception e) {

        }

        btnGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefCGoal = getActivity().getSharedPreferences(Integer.toString(((Home) getActivity()).getUser().getUserId()), Context.MODE_PRIVATE);
                calorieGoal = sharedPrefCGoal.getInt("calorieGoal", 0);
                tvGoal.setText(Integer.toString(calorieGoal) + " " + "Calories");
                //
                SharedPreferences sharedPrefCalorieGoal = getActivity().getSharedPreferences(Integer.toString(((Home) getActivity()).getUser().getUserId()), Context.MODE_PRIVATE);
                SharedPreferences.Editor editorCalorieGoal = sharedPrefCalorieGoal.edit();
                editorCalorieGoal.putInt("calorieGoal", calorieGoal);
                editorCalorieGoal.apply();
            }
        });

        btnSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSteps.setText(Integer.toString(totalSteps) + " " + "Steps");
                //
                SharedPreferences sharedPrefTotalSteps = getActivity().getSharedPreferences(Integer.toString(((Home) getActivity()).getUser().getUserId()), Context.MODE_PRIVATE);
                SharedPreferences.Editor editorTotalSteps = sharedPrefTotalSteps.edit();
                editorTotalSteps.putInt("totalSteps", totalSteps);
                editorTotalSteps.apply();
            }
        });

        btnConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTotalCaloriesConsumed asyncTaskObj = new GetTotalCaloriesConsumed();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String dateConsumed = sdf.format(date);
                    Integer userId = ((Home) getActivity()).getUser().getUserId();
                    String result = asyncTaskObj.execute(Integer.toString(userId), dateConsumed).get();
                    totalCaloriesConsumed = new JSONObject(result).getDouble("totalCaloriesConsumed");
                    tvConsumed.setText(Double.toString(totalCaloriesConsumed) + " " + "Calories");
                    //
                    SharedPreferences sharedPrefCaloriesConsumed = getActivity().getSharedPreferences(Integer.toString(((Home) getActivity()).getUser().getUserId()), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorCaloriesConsumed = sharedPrefCaloriesConsumed.edit();
                    editorCaloriesConsumed.putString("caloriesConsumed", Double.toString(totalCaloriesConsumed));
                    editorCaloriesConsumed.apply();
                } catch (Exception e) {

                }

            }
        });

        btnBurned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer userId = ((Home) getActivity()).getUser().getUserId();
                GetCaloriesBurnedSteps burnedStepsAsyncResult = new GetCaloriesBurnedSteps();
                GetCaloriesBurnedRest burnedRestAsyncResult = new GetCaloriesBurnedRest();
                try {
                    String burnedSteps = burnedStepsAsyncResult.execute(userId).get();
                    String burnedRest = burnedRestAsyncResult.execute(userId).get();
                    double caloriesBurnedSteps = new JSONObject(burnedSteps).getDouble("caloriesBurnedSteps");
                    double caloriesBurnedRest = new JSONObject(burnedRest).getDouble("caloriesBurnedRest");
                    totalCaloriesBurned = caloriesBurnedRest + (caloriesBurnedSteps * totalSteps);
                    tvBurned.setText(new DecimalFormat("#.##").format(totalCaloriesBurned) + " " + "Calories");
                    //
                    SharedPreferences sharedPrefCaloriesBurned = getActivity().getSharedPreferences(Integer.toString(((Home) getActivity()).getUser().getUserId()), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorCaloriesBurned = sharedPrefCaloriesBurned.edit();
                    editorCaloriesBurned.putString("caloriesBurned", Double.toString(totalCaloriesBurned));
                    editorCaloriesBurned.apply();
                } catch (Exception e) {

                }
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer userId = ((Home) getActivity()).getUser().getUserId();
                GetUserAsyncTask task = new GetUserAsyncTask();
                try {
                    String user = task.execute(userId).get();
                    JSONObject userObj = new JSONObject(user);
                    Integer uId = userObj.getInt("userId");
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
                    Users u = new Users(uId, firstName, surname, email, dob, height, weight, gender, address, postcode, levelOfActivity, stepsPerMile);
                    Report reportEntry = new Report(null, u, reportDate, totalCaloriesConsumed, totalCaloriesBurned, totalSteps, calorieGoal);
                    PostReportEntryAsyncTask postReportEntry = new PostReportEntryAsyncTask();
                    postReportEntry.execute(reportEntry);
                } catch (Exception e) {
                }
                new DeleteDatabase().execute();
                getContext().getSharedPreferences(Integer.toString(userId), 0).edit().clear().apply();
            }
        });
        return vCalorieTracker;
    }

    private class GetTotalCaloriesConsumed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getCaloriesConsumed(Integer.parseInt(params[0]), params[1]);
        }
    }

    private class GetCaloriesBurnedSteps extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            return RestClient.getCaloriesBurnedSteps(params[0]);
        }
    }

    private class GetCaloriesBurnedRest extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            return RestClient.getCaloriesBurnedRest(params[0]);
        }
    }

    private class GetTotalStepsAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            Integer userId = ((Home) getActivity()).getUser().getUserId();
            return db.DailyStepsDao().stepsSum(userId);
        }
    }

    private class GetUserAsyncTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            return RestClient.findByUserID(params[0]);
        }
    }

    private class PostReportEntryAsyncTask extends AsyncTask<Report, Void, Void> {
        @Override
        protected Void doInBackground(Report... params) {
            RestClient.postReportEntry(params[0]);
            return null;
        }
    }

    private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            db.DailyStepsDao().deleteAll();
            return null;
        }

    }
}
