package com.chatroom.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Message {
    
    public static final List<Message> msgList = new ArrayList();

    private long dateTime;
    private String sessionId;
    private String sender;
    private String msg;
    private String color;
    private HashMap<String, Boolean> pushMap = new HashMap();

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public HashMap<String, Boolean> getPushMap() {
        return pushMap;
    }

    public void setPushMap(HashMap<String, Boolean> pushMap) {
        this.pushMap = pushMap;
    }

}
