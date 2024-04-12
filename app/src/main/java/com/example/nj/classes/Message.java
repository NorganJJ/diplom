package com.example.nj.classes;

import java.util.Date;

public class Message {
    public String userName;
    public String textMessage;
    private long messageTime;

    public String messageCode;

    public String messageType;

    public String messageReference;

    public String messageFormat;

    public Message(){}


    public Message(String userName, String textMessage, String messageCode, String messageType, String messageReference, String messageFormat) {
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageCode = messageCode;
        this.messageTime = new Date().getTime();
        this.messageType = messageType;
        this.messageReference = messageReference;
        this.messageFormat = messageFormat;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageReference() {
        return messageReference;
    }

    public void setMessageReference(String messageReference) {
        this.messageReference = messageReference;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}
