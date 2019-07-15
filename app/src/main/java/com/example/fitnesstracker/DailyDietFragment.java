package com.example.fitnesstracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DailyDietFragment extends Fragment {
    private int check = 0;
    private View vDailyDiet;
    private TextView tvSOne, tvSTwo, tvSearch;
    private Spinner spFoodCategory, spFoodItem;
    private Button btnDdsSubmit, btnSearch;
    private EditText etServing;
    private JSONArray foodItemArray;
    private String foodCategory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDailyDiet = inflater.inflate(R.layout.fragment_daily_diet, container, false);
        tvSOne =  vDailyDiet.findViewById(R.id.tv_spinner_one);
        tvSTwo =  vDailyDiet.findViewById(R.id.tv_spinner_two);
        tvSearch = vDailyDiet.findViewById(R.id.tv_search);
        spFoodCategory = vDailyDiet.findViewById(R.id.sp_food_category);
        spFoodItem = vDailyDiet.findViewById(R.id.sp_food_item);
        etServing = vDailyDiet.findViewById(R.id.et_serving);
        btnDdsSubmit = vDailyDiet.findViewById(R.id.btn_dds_submit);
        btnSearch = vDailyDiet.findViewById(R.id.btn_search);
        List<String> list = new ArrayList<String>();
        list.add("meat");list.add("drink");list.add("dairy");list.add("bread");list.add("nuts");
        list.add("fruit");list.add("vegetable");list.add("grain");list.add("sea food");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFoodCategory.setAdapter(spinnerAdapter);
        spFoodCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                foodCategory = parent.getItemAtPosition(position).toString();
                if (foodCategory != null) {
                    //Toast.makeText(parent.getContext(), "food selected is " + foodCategory, Toast.LENGTH_LONG).show();
                }
                Log.i("food category", foodCategory);
                try {
                    GetByFoodCategoryAsyncTask foodCategoryAsyncTask = new GetByFoodCategoryAsyncTask();
                    String foodItem = foodCategoryAsyncTask.execute(foodCategory).get();
                    foodItemArray = new JSONArray(foodItem);
                    List<String> foodItemList = new ArrayList<String>();
                    for (int i = 0; i < foodItemArray.length(); i++) {
                        JSONObject foodObj = foodItemArray.getJSONObject(i);
                        String foodName = foodObj.getString("foodName");
                        foodItemList.add(foodName);
                        Log.i("food ", foodObj.toString());
                    }
                    ArrayAdapter<String> spinnerAdapterTwo = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, foodItemList);
                    spinnerAdapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spFoodItem.setAdapter(spinnerAdapterTwo);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnDdsSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String servingAmount = etServing.getText().toString();
                //Log.i("food arr ", foodItemArray.toString());
                if (servingAmount.isEmpty()) {
                    etServing.setError("Serving is required!");
                    return;
                }
                double serving = Double.parseDouble(servingAmount);
                Users currUser = ((Home)getActivity()).getUser();
                int foodItemInArrayIndex = 0;
                try {
                    String selectedFoodItem = spFoodItem.getSelectedItem().toString();
                    for (int i = 0; i < foodItemArray.length(); i++) {
                        JSONObject foodObj = foodItemArray.getJSONObject(i);
                        String foodName = foodObj.getString("foodName");
                        if (foodName.equals(selectedFoodItem)){
                            foodItemInArrayIndex = i;
                        }
                        Log.i("selected food item ", foodItemArray.get(foodItemInArrayIndex).toString());
                    }
                    JSONObject obj = foodItemArray.getJSONObject(foodItemInArrayIndex);
                    Food food = new Food(obj.getInt("foodId"), obj.getString("foodName"), obj.getString("foodCategory"), obj.getDouble("calorieAmount"), obj.getString("servingUnit"), obj.getDouble("servingAmount"), obj.getDouble("fat"));
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = new Date();
                        Date dateConsumed = sdf2.parse(sdf2.format(date));
                        Consumption consumptionEntry = new Consumption(null, currUser, food, dateConsumed, serving);
                        PostConsumptionAsyncTask postConsumption = new PostConsumptionAsyncTask();
                        postConsumption.execute(consumptionEntry);
                    }catch (ParseException e){

                    }
                }catch(JSONException e){
                    //json exception
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSearchFragment fragment = new GoogleSearchFragment();
                Bundle args = new Bundle();
                args.putString("foodCategory", foodCategory);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

            }
        });
        return vDailyDiet;
    }
    private class GetByFoodCategoryAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getByFoodCategory(params[0]);
        }
    }
    private class PostConsumptionAsyncTask extends AsyncTask<Consumption, Void, Void> {
        @Override
        protected Void doInBackground(Consumption... params) {
            RestClient.postConsumption(params[0]);
            return null;
        }
    }

}

