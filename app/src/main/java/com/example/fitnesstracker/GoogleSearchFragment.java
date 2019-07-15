package com.example.fitnesstracker;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;

public class GoogleSearchFragment extends Fragment {
    private View vGoogleSearch;
    private TextView tvWiki, tvAPI;
    private Button btnSearchGoogle, btnAddFood;
    private EditText etAskGoogle;
    private ImageView simpleImageView;
    private Food foodItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vGoogleSearch = inflater.inflate(R.layout.fragment_google_search, container, false);
        etAskGoogle = vGoogleSearch.findViewById(R.id.et_ask_google) ;
        tvWiki = vGoogleSearch.findViewById(R.id.tv_wiki);
        tvAPI = vGoogleSearch.findViewById(R.id.tv_api);
        btnSearchGoogle = vGoogleSearch.findViewById(R.id.btn_search_google);
        btnAddFood = vGoogleSearch.findViewById(R.id.btn_add_food_entry);
        simpleImageView = vGoogleSearch.findViewById(R.id.simpleImageView);
        btnSearchGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = etAskGoogle.getText().toString();
                SearchAsyncTask searchAsyncTask=new SearchAsyncTask();
                searchAsyncTask.execute(keyword);
                FoodAPISearchAsyncTask asyncTask = new FoodAPISearchAsyncTask();
                try {
                    String result = asyncTask.execute(keyword).get();
                    JSONObject foodObj = new JSONObject(result);
                    double calories = foodObj.getJSONArray("hints").getJSONObject(0).getJSONObject("food").getJSONObject("nutrients").getDouble("ENERC_KCAL");
                    double fat = foodObj.getJSONArray("hints").getJSONObject(0).getJSONObject("food").getJSONObject("nutrients").getDouble("FAT");
                    String measure = foodObj.getJSONArray("hints").getJSONObject(0).getJSONArray("measures").getJSONObject(0).getString("label");
                    double servingAmount = 1;
                    if (measure.toLowerCase().equals("gram") || measure.toLowerCase().equals("grams")){
                        servingAmount += 99;
                    }
                    String foodCategory = getArguments().getString("foodCategory");
                    foodItem = new Food(null, keyword, foodCategory, Double.parseDouble(new DecimalFormat("#.##").format(calories)), measure , servingAmount ,Double.parseDouble(new DecimalFormat("#.##").format(fat)));
                    tvAPI.setText("Calorie Amount: "+new DecimalFormat("#.##").format(calories) + "\r\n" +"Fat: "+ new DecimalFormat("#.##").format(fat) + "\r\n" + "Serving Unit: "+measure + "\r\n" +"Serving Amount: "+servingAmount);
                }catch (Exception e){
                }
            }
        });
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostFoodEntryAsyncTask foodAsyncT = new PostFoodEntryAsyncTask();
                foodAsyncT.execute(foodItem);
            }
        });
        return vGoogleSearch;
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return SearchGoogleAPI.search(params[0], new String[]{"num"}, new String[]{"1"});
        }
        @Override
        protected void onPostExecute(String result) {
            new GetImage().execute(SearchGoogleAPI.getImageURL(result));
            tvWiki.setText(SearchGoogleAPI.getSnippet(result));
        }
    }

    private class FoodAPISearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getFoodDetails(params[0]);
        }
    }

    private class PostFoodEntryAsyncTask extends AsyncTask<Food, Void, Void> {
        @Override
        protected Void doInBackground(Food... params) {
            RestClient.postFoodEntry(params[0]);
            return null;
        }
    }

    private class GetImage extends AsyncTask<String,Void, Bitmap>{
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){
                e.printStackTrace();
            }
            return logo;
        }
        protected void onPostExecute(Bitmap result){
            simpleImageView.setImageBitmap(result);
        }
    }
}

