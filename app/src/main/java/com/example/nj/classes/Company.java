package com.example.nj.classes;

import java.time.LocalDateTime;
import java.util.Date;

public class Company {

    public String uid;

    public String companyName;

    public String ogrn;

    public String companyView;

    public String companyTags;

    public Date dataFound;

    public String callingUser;

    public String companyLogo;

    public String adminPassword;

    public Company() {
    }

    public Company(String uid, String companyName, String ogrn, String companyView, String companyTags, Date dataFound, String callingUser, String companyLogo, String adminPassword) {
        this.uid = uid;
        this.companyName = companyName;
        this.ogrn = ogrn;
        this.companyView = companyView;
        this.companyTags = companyTags;
        this.dataFound = dataFound;
        this.callingUser = callingUser;
        this.companyLogo = companyLogo;
        this.adminPassword = adminPassword;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getCompanyView() {
        return companyView;
    }

    public void setCompanyView(String companyView) {
        this.companyView = companyView;
    }

    public String getCompanyTags() {
        return companyTags;
    }

    public void setCompanyTags(String companyTags) {
        this.companyTags = companyTags;
    }

    public Date getDataFound() {
        return dataFound;
    }

    public void setDataFound(Date dataFound) {
        this.dataFound = dataFound;
    }

    public String getCallingUser() {
        return callingUser;
    }

    public void setCallingUser(String callingUser) {
        this.callingUser = callingUser;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
