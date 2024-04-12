package com.example.nj.classes;

public class Invitation {

    public String uid;

    public String companyRecieverUid;

    public String userReciever;

    public Invitation() {
    }

    public Invitation(String uid, String companyRecieverUid, String userReciever) {
        this.uid = uid;
        this.companyRecieverUid = companyRecieverUid;
        this.userReciever = userReciever;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCompanyRecieverUid() {
        return companyRecieverUid;
    }

    public void setCompanyRecieverUid(String companyRecieverUid) {
        this.companyRecieverUid = companyRecieverUid;
    }

    public String getUserReciever() {
        return userReciever;
    }

    public void setUserReciever(String userReciever) {
        this.userReciever = userReciever;
    }
}
