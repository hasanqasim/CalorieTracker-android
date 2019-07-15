package com.example.fitnesstracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText etFirsName, etSurname, etEmail, etDob, etHeight, etWeight, etAddress, etSteps, etUsername, etPassword, etCPassword;
    private RadioGroup radioGrpGender;
    private RadioButton radioBtnGender;
    private Spinner spActivityLevel, spPostcode;
    private Button btnSubmit;
    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etFirsName = findViewById(R.id.et_firstName);
        etSurname = findViewById(R.id.et_surname);
        etEmail = findViewById(R.id.et_email);
        etDob = findViewById(R.id.et_dob);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etAddress = findViewById(R.id.et_address);
        etSteps = findViewById(R.id.et_Steps);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etCPassword = findViewById(R.id.et_confirm_password);
        radioGrpGender = findViewById(R.id.radio_gender);
        spPostcode= findViewById(R.id.sp_postcode);
        spActivityLevel= findViewById(R.id.sp_activityLevel);
        btnSubmit = findViewById(R.id.btn_submit);
        etDob.setKeyListener(null);
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                final DatePickerDialog picker = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etDob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    }
                    }, year, month, day);
                picker.show();
            }
        });

        List<String> list = new ArrayList<String>();
        for (int i = 1; i < 6; i++) {
            list.add(String.valueOf(i));
        }
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivityLevel.setAdapter(spinnerAdapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirsName.getText().toString();
                String surname = etSurname.getText().toString();
                email = etEmail.getText().toString();
                String dob = etDob.getText().toString();
                String height = etHeight.getText().toString();
                String weight = etWeight.getText().toString();
                String address = etAddress.getText().toString();
                String steps= etSteps.getText().toString();
                String postcode = spPostcode.getSelectedItem().toString();
                String activityLevel = spActivityLevel.getSelectedItem().toString();
                username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String cPassword = etCPassword.getText().toString();

                int selectedId = radioGrpGender.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioBtnGender = findViewById(selectedId);
                String gender = radioBtnGender.getText().toString().toLowerCase();
                // Validate user input
                if (firstName.isEmpty()) {
                    etFirsName.setError("First Name is required!");
                    return;
                }
                if (surname.isEmpty()) {
                    etSurname.setError("Surname is required!");
                    return;
                }
                if (email.isEmpty()) {
                    etEmail.setError("Email is required!");
                    return;
                }
                String regex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email);
                if (!matcher.matches()) {
                    etEmail.setError("Invalid Email!");
                }
                if (dob.isEmpty()) {
                    etDob.setError("Date of Birth is required!");
                    return;
                }
                if (height.isEmpty()) {
                    etHeight.setError("Height is required!");
                    return;
                }
                if (weight.isEmpty()) {
                    etWeight.setError("Weight is required!");
                    return;
                }
                if (address.isEmpty()) {
                    etAddress.setError("Address is required!");
                    return;
                }
                if (steps.isEmpty()) {
                    etSteps.setError("Steps per Mile are required!");
                    return;
                }
                if (username.isEmpty()) {
                    etUsername.setError("Username is required!");
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password is required!");
                    return;
                }
                if (cPassword.isEmpty()) {
                    etCPassword.setError("Password Confirmation is required!");
                    return;
                }
                if (!password.equals(cPassword)){
                    etCPassword.setError("Password Mismatch");
                }
                //new EmailGetAsyncTask().execute(email);
                new UsernameGetAsyncTask().execute(username);

            }
        });
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
    private class UserPostAsyncTask extends AsyncTask<Users, Void, Void>
    {
        @Override
        protected Void doInBackground(Users... user) {
            RestClient.createUser(user[0]);
            return null;
        }
    }
    private class CredentialPostAsyncTask extends AsyncTask<Credential, Void, Void>
    {
        @Override
        protected Void doInBackground(Credential... credentials) {
            RestClient.createCredential(credentials[0]);
            return null;
        }
    }
    private class UserIdGetAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return RestClient.getLastUserId();
        }
    }
    private class UsernameGetAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return RestClient.findByUsername(params[0]).isEmpty()?true:false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result){
                etUsername.setError("Username is already taken!");
                //return false;
            }
            else
                new EmailGetAsyncTask().execute(etEmail.getText().toString());
        }
    }
    private class EmailGetAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return RestClient.findByEmail(params[0]).isEmpty()?true:false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                etEmail.setError("email is already in use!");
                //return;
            }
            else{
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    java.util.Date dateOfBirth = sdf1.parse(etDob.getText().toString());
                    Date date = new Date();
                    Date signupDate = sdf2.parse(sdf2.format(date));
                    Users user = new Users(null, etFirsName.getText().toString(),etSurname.getText().toString(), email, dateOfBirth, Double.parseDouble(etWeight.getText().toString()), Double.parseDouble(etHeight.getText().toString()), radioBtnGender.getText().toString().toLowerCase(), etAddress.getText().toString(), spPostcode.getSelectedItem().toString(), Integer.parseInt(spActivityLevel.getSelectedItem().toString()), Integer.parseInt(etSteps.getText().toString()));
                    UserPostAsyncTask postUserAsyncTask=new UserPostAsyncTask();
                    postUserAsyncTask.execute(user);
                    try {
                        UserIdGetAsyncTask userIdGetAsyncTask=new UserIdGetAsyncTask();
                        Integer userId = Integer.parseInt(userIdGetAsyncTask.execute().get());
                        Users u = new Users(userId, etFirsName.getText().toString(),etSurname.getText().toString(), email, dateOfBirth, Double.parseDouble(etWeight.getText().toString()), Double.parseDouble(etHeight.getText().toString()), radioBtnGender.getText().toString().toLowerCase(), etAddress.getText().toString(), spPostcode.getSelectedItem().toString(), Integer.parseInt(spActivityLevel.getSelectedItem().toString()), Integer.parseInt(etSteps.getText().toString()));
                        String hashPassword = hash(etPassword.getText().toString());
                        Credential credentials = new Credential(username, hashPassword, signupDate, u);
                        CredentialPostAsyncTask postCredentialAsyncTask = new CredentialPostAsyncTask();
                        postCredentialAsyncTask.execute(credentials);
                        //Log.i("userId", userId.toString());
                    }catch (ExecutionException e){
                        e.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }catch (NoSuchAlgorithmException e){
                        e.printStackTrace();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(SignupActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
