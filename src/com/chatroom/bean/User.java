package com.chatroom.bean;

import java.util.HashMap;
import javax.websocket.Session;

public class User {

    public static final HashMap<String, User> userMap = new HashMap();

    private Session session;
    private long accessTime;
    private String nickname;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
