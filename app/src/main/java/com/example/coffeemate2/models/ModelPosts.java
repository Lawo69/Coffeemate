package com.example.coffeemate2.models;

public class ModelPosts {
    String uid, uname, udp, pid, ppost, ptime;

    public ModelPosts(){

    }

    public ModelPosts(String pid, String ppost, String ptime, String uid, String uname, String udp) {
        this.pid = pid;
        this.ppost = ppost;
        this.ptime = ptime;
        this.udp = udp;
        this.uid = uid;
        this.uname = uname;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUdp() {
        return udp;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPpost() {
        return ppost;
    }

    public void setPpost(String ppost) {
        this.ppost = ppost;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }
}
