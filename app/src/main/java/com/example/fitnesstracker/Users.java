package com.example.fitnesstracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Users implements Parcelable {
    private Integer userId;
    private String firstName;
    private String surname;
    private String email;
    private Date dob;
    private double height;
    private double weight;
    private String gender;
    private String address;
    private String postcode;
    private int levelOfActivity;
    private int stepsPerMile;

    public Users(Integer userId, String firstName,String surname,String email, Date dob, double height, double weight, String gender,String address,String postcode, int levelOfActivity, int stepsPerMile) {
        this.userId = userId;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.levelOfActivity = levelOfActivity;
        this.stepsPerMile = stepsPerMile;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userID) {
        this.userId = userId;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getLevelOfActivity() {
        return levelOfActivity;
    }

    public void setLevelOfActivity(int levelOfActivity) {
        this.levelOfActivity = levelOfActivity;
    }

    public int getStepsPerMile() {
        return stepsPerMile;
    }

    public void setStepsPerMile(int stepsPerMile) {
        this.stepsPerMile = stepsPerMile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.firstName);
        dest.writeString(this.surname);
        dest.writeString(this.email);
        dest.writeLong(this.dob != null ? this.dob.getTime() : -1);
        dest.writeDouble(this.height);
        dest.writeDouble(this.weight);
        dest.writeString(this.gender);
        dest.writeString(this.address);
        dest.writeString(this.postcode);
        dest.writeInt(this.levelOfActivity);
        dest.writeInt(this.stepsPerMile);
    }

    protected Users(Parcel in) {
        this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.firstName = in.readString();
        this.surname = in.readString();
        this.email = in.readString();
        long tmpDob = in.readLong();
        this.dob = tmpDob == -1 ? null : new Date(tmpDob);
        this.height = in.readDouble();
        this.weight = in.readDouble();
        this.gender = in.readString();
        this.address = in.readString();
        this.postcode = in.readString();
        this.levelOfActivity = in.readInt();
        this.stepsPerMile = in.readInt();
    }

    public static final Parcelable.Creator<Users> CREATOR = new Parcelable.Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel source) {
            return new Users(source);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };
}
