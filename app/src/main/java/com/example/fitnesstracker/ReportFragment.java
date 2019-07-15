package com.example.fitnesstracker;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class ReportFragment extends Fragment {
    private View vReport;
    private static String TAG = "MainActivity";

    private float[] yData = new float[3];
    private String[] xData = {"Total Calories Consumed", "Total Calories Burned" , "Remaining Calories"};
    private PieChart pieChart;
    private EditText etReportDate;
    private Button btnGetReport, btnBarChart;
    private double[] dataY;
    private String[] dataX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vReport = inflater.inflate(R.layout.fragment_report, container, false);
        pieChart = vReport.findViewById(R.id.idPieChart);
        etReportDate = vReport.findViewById(R.id.et_report_date);
        btnGetReport = vReport.findViewById(R.id.btn_get_report);
        btnBarChart = vReport.findViewById(R.id.btn_get_bar_chart);
        etReportDate.setKeyListener(null);
        etReportDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                final DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etReportDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                picker.show();

            }
        });
        btnGetReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportDate = etReportDate.getText().toString();
                Integer uID = ((Home)getActivity()).getUser().getUserId();
                GetReportByDateANDIdAsyncTask asyncTask = new GetReportByDateANDIdAsyncTask();
                try {
                    String report = asyncTask.execute(uID.toString(), reportDate).get();
                    JSONArray reportJsonArray = new JSONArray(report);
                    yData[0] = (float)reportJsonArray.getJSONObject(0).getDouble("caloriesConsumed");
                    yData[1] = (float)reportJsonArray.getJSONObject(0).getDouble("caloriesBurned");
                    yData[2] = (float)reportJsonArray.getJSONObject(0).getDouble("remainingCalories");
                    pieChart.getDescription().setText(null);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setRotationEnabled(true);
                    pieChart.setTransparentCircleAlpha(0);
                    pieChart.setHoleRadius(5f);
                    pieChart.setUsePercentValues(true);
                    pieChart.getLegend().setTextColor(Color.BLACK);

                    addDataSet();

                }catch(ExecutionException e){

                }catch(InterruptedException e){

                }catch(JSONException e){

                }
            }
        });
        btnBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReportDate.setText("");
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new BarChartFragment()).addToBackStack(null).commit();

            }
        });
        return vReport;
    }
    private void addDataSet() {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry((yData[i]), xData[i]));
        }
        for (int i = 0; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        //create data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
        colors.add(Color.GRAY);

        pieDataSet.setColors(colors);

        // instantiate pie data object now
        //PieData data = new PieData(xEntrys, pieDataSet);


        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(20f);
        pieData.setValueTextColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
    private class GetReportByDateANDIdAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getReportByUserIdANDReportDate(Integer.parseInt(params[0]), params[1]);
        }
    }
}

