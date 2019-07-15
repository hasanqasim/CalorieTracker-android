package com.example.fitnesstracker;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StepsFragment extends Fragment {
    private View vSteps;
    private List<HashMap<String, String>> stepsListArray;
    private SimpleAdapter myListAdapter;
    private ListView stepsListView;
    private Button addButton;
    private EditText stepsEditText;
    private String time, updatedTime;
    private EditText editText;
    private DailyStepsDatabase db = null;
    private AlarmManager alarmMgr;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;


    String[] colHEAD = new String[] {"TIME", "STEPS"};
    int[] dataCell = new int[] {R.id.tv_time,R.id.tv_steps};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vSteps = inflater.inflate(R.layout.fragment_steps, container, false);
        db = Room.databaseBuilder(getContext().getApplicationContext(),
                DailyStepsDatabase.class, "DailyStepsDatabase")
                .fallbackToDestructiveMigration()
                .build();
        stepsListView = vSteps.findViewById(R.id.list_view);
        addButton = vSteps.findViewById(R.id.addButton);
        stepsEditText = vSteps.findViewById(R.id.addEditText);
        stepsListArray = new ArrayList<>();
        ReadDatabase readDatabase = new ReadDatabase();
        readDatabase.execute();
        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                time = sdf.format(new Date());
                String steps = stepsEditText.getText().toString();
                if (steps.isEmpty()){
                    stepsEditText.setError("steps cannot be empty");
                    return;
                }
                InsertDatabase addDatabase = new InsertDatabase();
                addDatabase.execute();
                HashMap<String, String> map = new HashMap<>();
                map.put("TIME", time);
                map.put("STEPS", steps);
                addMap(map);
                stepsEditText.setText("");
            }
        });
        stepsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show input box
                showInputBox(stepsListArray.get(position).get("STEPS"),position);
            }
        });
        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(getContext(), ScheduledIntentService.class);
        pendingIntent = PendingIntent.getService(getContext(), 0, alarmIntent, 0);
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        return vSteps;
    }

    private class InsertDatabase extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            if (!(stepsEditText.getText().toString().isEmpty())) {
                String details = stepsEditText.getText().toString();
                Integer userId = ((Home)getActivity()).getUser().getUserId();
                DailySteps steps = new DailySteps(userId, time, Integer.parseInt(details));
                long id = db.DailyStepsDao().insert(steps);
            }
            return "";
        }
    }
    public void showInputBox(String oldItem, final int index){
        final Dialog dialog=new Dialog(getContext());
        dialog.setTitle("Input Box");
        dialog.setContentView(R.layout.input_box);
        TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
        txtMessage.setText("Update Steps Taken");
        txtMessage.setTextColor(Color.parseColor("#ff2222"));
        editText=(EditText)dialog.findViewById(R.id.txtinput);
        editText.setText(oldItem);
        Button bt=(Button)dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> mymap = new HashMap<String, String>();
                updatedTime = stepsListArray.get(index).get("TIME");
                mymap.put("TIME", updatedTime);
                mymap.put("STEPS", editText.getText().toString());
                stepsListArray.set(index, mymap);
                myListAdapter.notifyDataSetChanged();
                dialog.dismiss();
                UpdateDatabase updateDatabase = new UpdateDatabase();
                updateDatabase.execute();

            }
        });
        dialog.show();
    }

    private class ReadDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Integer userId = ((Home) getActivity()).getUser().getUserId();
            List<DailySteps> stepsEntry = db.DailyStepsDao().findByID(userId);
           // List<DailySteps> AllstepsEntry = db.DailyStepsDao().getAll();

            if (!(stepsEntry.isEmpty() || stepsEntry == null)) {
                for (DailySteps step : stepsEntry) {
                    String timeStr = step.getTime();
                    String stepStr = Integer.toString(step.getSteps());
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("TIME", timeStr);
                    map.put("STEPS", stepStr);
                    stepsListArray.add(map);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            setView();
        }
    }

    private class UpdateDatabase extends AsyncTask<Void, Void, Void> {
        @Override protected Void doInBackground(Void... params) {
            DailySteps entry = null;
            entry = db.DailyStepsDao().findByTime(updatedTime);
            if (entry!=null) {
                entry.setSteps(Integer.parseInt(editText.getText().toString()));
                db.DailyStepsDao().updateStepsEntry(entry);
            }
            return null;
        }
    }

    protected void addMap(HashMap map){
        stepsListArray.add(map);
        myListAdapter = new SimpleAdapter(getContext(),stepsListArray,R.layout.list_view,colHEAD,dataCell);
        stepsListView.setAdapter(myListAdapter);
    }

    private void setView(){
        myListAdapter = new SimpleAdapter(getContext(),stepsListArray,R.layout.list_view,colHEAD,dataCell);
        stepsListView.setAdapter(myListAdapter);
    }
}
