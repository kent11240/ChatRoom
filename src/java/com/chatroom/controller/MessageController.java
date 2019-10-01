package com.chatroom.controller;

import com.chatroom.bean.Message;
import com.chatroom.service.MessageService;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

@WebServlet(name = "MessageController", urlPatterns = {"/msg.do"})
public class MessageController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(WebSocketController.class);
    
    private static final MessageService service = new MessageService();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        try {
            Method m = MessageController.class.getDeclaredMethod(op, HttpServletRequest.class, HttpServletResponse.class);
            m.invoke(this, request, response);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("MessageController.processRequest:", ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void uploadMsg(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = request.getParameter("key");
        String sender = request.getParameter("sender");
        String msg = request.getParameter("msg");
        String color = request.getParameter("color");
        
        Message msgBean = service.createMsg(sessionId, sender, msg, color);
        service.saveMsg(msgBean);
    }
}
