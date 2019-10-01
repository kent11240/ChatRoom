package com.chatroom.service;

import com.chatroom.bean.Message;
import org.json.JSONArray;
import org.json.JSONObject;

public class MessageService {

    public Message createMsg(String sessionId, String sender, String msg, String color) {
        Message msgBean = new Message();

        msgBean.setDateTime(System.currentTimeMillis());
        msgBean.setSessionId(sessionId);
        msgBean.setSender(sender);
        msgBean.setMsg(msg);
        msgBean.setColor(color);

        return msgBean;
    }

    public void saveMsg(Message msgBean) {
        Message.msgList.add(msgBean);
    }

    public JSONArray getNewMsgs(String sessionId) {
        JSONArray ja = new JSONArray();

        for (int i = 0; i < Message.msgList.size(); i++) {
            Message msgBean = Message.msgList.get(i);
            if (!msgBean.getSessionId().equals(sessionId)
                    && (msgBean.getPushMap().get(sessionId) == null || msgBean.getPushMap().get(sessionId) == false)) {
                msgBean.getPushMap().put(sessionId, true);

                JSONObject jo = new JSONObject();
                jo.put("sender", msgBean.getSender());
                jo.put("msg", msgBean.getMsg());
                jo.put("color", msgBean.getColor());

                ja.put(jo);
            }
        }

        return ja;
    }
    
    public void readAllMsg(String sessionId) {
        for (int i = 0; i < Message.msgList.size(); i++) {
            Message msgBean = Message.msgList.get(i);
            msgBean.getPushMap().put(sessionId, true);
        }
    }
    
    public void cleanMsg() {
        for (int i = 0; i < Message.msgList.size(); i++) {
            Message msgBean = Message.msgList.get(i);
            if (System.currentTimeMillis() - msgBean.getDateTime() >= 30000) {
                Message.msgList.remove(msgBean);
            }
        }
    }
}
