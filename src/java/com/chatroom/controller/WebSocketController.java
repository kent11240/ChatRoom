package com.chatroom.controller;

import com.chatroom.bean.User;
import com.chatroom.service.MessageService;
import com.chatroom.service.UserSerivce;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@ServerEndpoint("/ws/{nickname}")
public class WebSocketController {

    private static final Logger logger = Logger.getLogger(WebSocketController.class);

    private String sessionId;

    private static final UserSerivce userService = new UserSerivce();
    private static final MessageService messageService = new MessageService();

    @OnOpen
    public void openConnection(Session session, @PathParam("nickname") String nickname) {
        userService.createUser(session, nickname);

        this.sessionId = session.getId();
        logger.info(String.format("User:[%s] openConnection!", nickname));
        logger.info(String.format("Online Connections:%d", User.userMap.size()));

        pushOnlineList();
    }

    @OnMessage
    public String wsHandler(String incomingMessage, Session session) {
        JSONObject jo = new JSONObject(incomingMessage);
        JSONObject result = new JSONObject();

        try {
            Method m = WebSocketController.class.getDeclaredMethod(jo.getString("op"), JSONObject.class);
            result = (JSONObject) m.invoke(this, jo);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("WebSocketController.wsHandler:", ex);
        }

        result.put("op", jo.getString("op"));
        return result.toString();
    }

    @OnError
    public void throwError(Throwable t) {
        logger.error("WebSocketController.throwError:", t);
    }

    @OnClose
    public void closeConnection(Session session) {
        User user = userService.getUser(session.getId());
        if (user != null) {
            logger.info(String.format("User:[%s] closeConnection!", user.getNickname()));
        }

        userService.removeUser(session.getId());
        logger.info(String.format("Online Connections:%d", User.userMap.size()));
        
        pushOnlineList();
    }

    private JSONObject getSessionId(JSONObject rq) {
        messageService.readAllMsg(sessionId);

        JSONObject rs = new JSONObject();
        rs.put("key", sessionId);
        return rs;
    }

    private JSONObject pushMsgs(JSONObject rq) {
        messageService.cleanMsg();

        for (int i = 0; i < User.userMap.keySet().size(); i++) {
            Session singleSession = User.userMap.get(User.userMap.keySet().toArray()[i]).getSession();
            JSONObject rs = new JSONObject();
            JSONArray data = messageService.getNewMsgs(singleSession.getId());
            rs.put("op", "getMsgs");
            rs.put("data", data);

            try {
                singleSession.getBasicRemote().sendText(rs.toString());
            } catch (IOException ex) {
                logger.error("WebSocketController.pushMsgs:", ex);
            }
        }

        return new JSONObject();
    }
    
    private void pushOnlineList() {
        JSONObject rs = new JSONObject();
        JSONArray data = userService.getOnlineList();
        rs.put("op", "getOnlineList");
        rs.put("data", data);
        for (int i = 0; i < User.userMap.keySet().size(); i++) {
            Session singleSession = User.userMap.get(User.userMap.keySet().toArray()[i]).getSession();
            try {
                singleSession.getBasicRemote().sendText(rs.toString());
            } catch (IOException ex) {
                logger.error("WebSocketController.pushOnlineList:", ex);
            }
        }
    }
}
