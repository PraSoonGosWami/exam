package com.example.exam.Models;

public class UserProfile {
    String name;
    String uid;
    String email;

    public UserProfile(String name, String uid, String email) {
        this.name = name;
        this.uid = uid;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}
