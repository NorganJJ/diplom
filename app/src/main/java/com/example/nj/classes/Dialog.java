package com.example.nj.classes;

public class Dialog {

    public String uid;

    public String actualUser;

    public String userReceiver;

    public String lastMessage;

    public int newMessages;

    public Dialog() {
    }

    public Dialog(String uid, String actualUser, String userReceiver, String lastMessage, int newMessages) {
        this.uid = uid;
        this.actualUser = actualUser;
        this.userReceiver = userReceiver;
        this.lastMessage = lastMessage;
        this.newMessages = newMessages;
    }

    public int getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(int newMessages) {
        this.newMessages = newMessages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getActualUser() {
        return actualUser;
    }

    public void setActualUser(String actualUser) {
        this.actualUser = actualUser;
    }

    public String getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(String userReceiver) {
        this.userReceiver = userReceiver;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
