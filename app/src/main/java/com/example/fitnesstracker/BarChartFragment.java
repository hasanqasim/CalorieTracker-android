package com.example.fitnesstracker;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BarChartFragment extends Fragment {
    private View vBarChart;
    private EditText etStartDate, etEndDate;
    private Button btnBarChart;
    private BarChart barChart;
    private List<BarEntry> bargroup1, bargroup2;
    private List<String> labels;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vBarChart = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        etStartDate = vBarChart.findViewById(R.id.et_start_date);
        etEndDate = vBarChart.findViewById(R.id.et_end_date);
        btnBarChart = vBarChart.findViewById(R.id.btn_bar_chart);
        barChart = vBarChart.findViewById(R.id.barchart);
        bargroup1 = new ArrayList<>();
        bargroup2 = new ArrayList<>();
        labels = new ArrayList<>();
        etStartDate.setKeyListener(null);
        etStartDate.setOnClickListener(new View.OnClickListener() {
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
                        etStartDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        etEndDate.setKeyListener(null);
        etEndDate.setOnClickListener(new View.OnClickListener() {
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
                        etEndDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        btnBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportStartDate = etStartDate.getText().toString();
                String reportEndDate = etEndDate.getText().toString();
                Integer uID = ((Home)getActivity()).getUser().getUserId();
                GetBarChartReportAsyncTask asyncTask = new GetBarChartReportAsyncTask();
                try {
                    String report = asyncTask.execute(uID.toString(), reportStartDate, reportEndDate).get();
                    JSONArray reportJsonArray = new JSONArray(report);
                    for (int i = 0; i < reportJsonArray.length(); i++) {
                        JSONObject obj = reportJsonArray.getJSONObject(i);
                        bargroup1.add(new BarEntry(i, (float) obj.getDouble("totalCaloriesConsumed")));
                        bargroup2.add(new BarEntry(i,(float) obj.getDouble("totalCaloriesBurned")));
                        labels.add(obj.getString("reportDate"));
                    }
                    BarDataSet barDataSet1 = new BarDataSet(bargroup1, "Consumed");
                    barDataSet1.setColor(Color.BLUE);
                    BarDataSet barDataSet2 = new BarDataSet(bargroup2, "Burned");
                    barDataSet1.setColor(Color.RED);
                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet1);
                    dataSets.add(barDataSet2);
                    BarData data = new BarData(dataSets);
                    barChart.setData(data);
                    //
                    XAxis xl = barChart.getXAxis();
                    xl.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xl.setGranularity(1);
                    xl.setGranularityEnabled(true);
                    xl.setCenterAxisLabels(true);
                    //
                    barChart.setDragEnabled(true);
                    barChart.getAxisRight().setDrawLabels(false);
                    barChart.setVisibleXRangeMaximum(4);
                    barChart.getDescription().setEnabled(false);
                    float groupSpace = 0.32f;
                    float barSpace = 0.1f;
                    data.setBarWidth(0.24f);
                    barChart.getXAxis().setAxisMinimum(0);
                    barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace, barSpace)*bargroup1.size());
                    barChart.getAxisLeft().setAxisMinimum(0);
                    barChart.groupBars(0, groupSpace, barSpace);
                    barChart.invalidate();


                }catch(ExecutionException e){

                }catch(InterruptedException e){

                }catch(JSONException e){

                }
            }
        });
        return vBarChart;
        }
    private class GetBarChartReportAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getBarChartReport(Integer.parseInt(params[0]), params[1], params[2]);
        }
    }

}


