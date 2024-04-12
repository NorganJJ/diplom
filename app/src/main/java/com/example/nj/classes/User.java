package com.example.nj.classes;

public class User {

    public String uid;

    public String mail;

    public String password;

    public int age;

    public String nickName;

    public String realName;

    public String surName;

    public String secondName;

    public String photo;

    public String company_UID;

    public User(){

    }

    public User(String uid, String mail, String password, int age, String nickName, String realName, String surName, String secondName, String photo, String company_UID) {
        this.uid = uid;
        this.mail = mail;
        this.password = password;
        this.age = age;
        this.nickName = nickName;
        this.realName = realName;
        this.surName = surName;
        this.secondName = secondName;
        this.photo = photo;
        this.company_UID = company_UID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCompany_UID() {
        return company_UID;
    }

    public void setCompany_UID(String company_UID) {
        this.company_UID = company_UID;
    }
}
