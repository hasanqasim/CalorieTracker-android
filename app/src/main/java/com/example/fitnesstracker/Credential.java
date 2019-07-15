package com.example.fitnesstracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Credential implements Parcelable {
    private String username;
    private String passwordHash;
    private Date signupDate;
    private Users userId;


    public Credential(String username, String passwordHash, Date signupDate, Users userId){
        this.username = username;
        this.passwordHash = passwordHash;
        this.signupDate = signupDate;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(Date signupDate) {
        this.signupDate = signupDate;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userId, flags);
        dest.writeString(this.username);
        dest.writeString(this.passwordHash);
        dest.writeLong(this.signupDate != null ? this.signupDate.getTime() : -1);
    }

    protected Credential(Parcel in) {
        this.userId = in.readParcelable(Users.class.getClassLoader());
        this.username = in.readString();
        this.passwordHash = in.readString();
        long tmpSignupDate = in.readLong();
        this.signupDate = tmpSignupDate == -1 ? null : new Date(tmpSignupDate);
    }

    public static final Creator<Credential> CREATOR = new Creator<Credential>() {
        @Override
        public Credential createFromParcel(Parcel source) {
            return new Credential(source);
        }

        @Override
        public Credential[] newArray(int size) {
            return new Credential[size];
        }
    };
}
