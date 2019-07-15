package com.example.fitnesstracker;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsername = findViewById(R.id.et_loginUsername);
        etPassword = findViewById(R.id.et_loginPassword);
        Button signupBtn = (Button) findViewById(R.id.btnSignup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        Button loginBtn = (Button) findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                try {
                    UserCredentialGetAsyncTask userCredentialGetAsyncTask = new UserCredentialGetAsyncTask();
                    String userCredential = userCredentialGetAsyncTask.execute(username).get();
                    JSONObject jsonObject = new JSONObject(userCredential);

                    String getUsername = jsonObject.getString("username");
                    if (!username.equals(getUsername)){
                        etUsername.setError("Username does not exist!");
                        return;
                    }
                    String getPasswordHash = jsonObject.getString("passwordHash");
                    //Log.i("user",jsonUser.toString());
                    String passwordHash = hash(password);
                    if (passwordHash.equals(getPasswordHash)){
                        JSONObject jsonUser = new JSONObject(jsonObject.getString("userId"));
                        String dob = jsonUser.getString("dob");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date dateOfBirth = sdf1.parse(dob);
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        Bundle bundle=new Bundle();
                        Users currentUser = new Users(jsonUser.getInt("userId"), jsonUser.getString("firstName"), jsonUser.getString("surname"), jsonUser.getString("email"), dateOfBirth,jsonUser.getDouble("height"), jsonUser.getDouble("weight"), jsonUser.getString("gender"), jsonUser.getString("address"), jsonUser.getString("postcode"), jsonUser.getInt("levelOfActivity"), jsonUser.getInt("stepsPerMile"));
                        //Log.i("name",jsonUser.getString("firstName"));
                        //bundle.putString("firstName", jsonUser.getString("firstName"));
                        bundle.putParcelable("currentUser", currentUser);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }else{
                        etPassword.setError("Incorrect Password!");
                        return;
                    }
                }catch (ExecutionException e){
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }catch (ParseException e){

                }
            }
        });
    }
    private class UserCredentialGetAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.getCredentialByUsername(params[0]);
        }
    }
    public String hash(String p) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(p.getBytes(Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

