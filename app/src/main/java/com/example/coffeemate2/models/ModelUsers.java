package com.example.coffeemate2.models;

public class ModelUsers {

    //use same name as in firebase database
    String email, uid, fullname,profileimage,search;

    public ModelUsers(){

    }
    public ModelUsers(String email, String fullname, String profileimage, String search){
        this.email = email;
        this.fullname = fullname;
        this.profileimage = profileimage;
        this.search = search;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
