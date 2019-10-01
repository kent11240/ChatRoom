package com.chatroom.service;

import com.chatroom.bean.Message;
import com.chatroom.bean.User;
import java.io.IOException;
import javax.websocket.Session;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserSerivce {

    public User createUser(Session session, String nickname) {
        User userBean = new User();

        userBean.setSession(session);
        userBean.setAccessTime(System.currentTimeMillis());
        userBean.setNickname(nickname);

        User.userMap.put(userBean.getSession().getId(), userBean);

        return userBean;
    }

    public void removeUser(String wsSessionId) {
        User.userMap.remove(wsSessionId);
    }

    public User getUser(String wsSessionId) {
        return User.userMap.get(wsSessionId);
    }
    
    public JSONArray getOnlineList() {
        JSONArray ja = new JSONArray();

        for (int i = 0; i < User.userMap.keySet().size(); i++) {
            User user = User.userMap.get(User.userMap.keySet().toArray()[i]);
            JSONObject jo = new JSONObject();
            jo.put("id", user.getSession().getId());
            jo.put("nickName", user.getNickname());
            
            ja.put(jo);
        }

        return ja;
    }
}
