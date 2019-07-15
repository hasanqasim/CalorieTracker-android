package com.example.fitnesstracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class MainFragment extends Fragment {
    private View vMain;
    private TextView tvWelcome;
    private TextClock tcTimer;
    private EditText etCalorieGoal;
    private Button btnSubmit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMain = inflater.inflate(R.layout.fragment_main, container, false);
        tvWelcome =  vMain.findViewById(R.id.tv_welcome);
        tcTimer = vMain.findViewById(R.id.textClock);
        tcTimer.setFormat12Hour(null);
        String firstName = ((Home)getActivity()).getUser().getFirstName();
        Log.i("name", firstName);
        tvWelcome.setText("Welcome " + firstName);
        etCalorieGoal = vMain.findViewById(R.id.et_calorieGoal);
        btnSubmit =  vMain.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calorieGoal = etCalorieGoal.getText().toString();
                try
                {
                    Integer cGoal = Integer.parseInt(calorieGoal);
                    TextView textView = (TextView) vMain.findViewById(R.id.tv_cg);
                    textView.setText("Calorie Goal: " + cGoal);
                    etCalorieGoal.getText().clear();
                    SharedPreferences sharedPrefCGoal = getActivity().getSharedPreferences(Integer.toString(((Home)getActivity()).getUser().getUserId()), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorCGoal = sharedPrefCGoal.edit();
                    editorCGoal.putInt("calorieGoal", cGoal);
                    editorCGoal.apply();

                }
                catch (NumberFormatException e){
                    etCalorieGoal.setError("Invalid Calorie Goal");
                }
            } });
        return vMain;
    }
}

