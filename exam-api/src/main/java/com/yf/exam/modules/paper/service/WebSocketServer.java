package com.yf.exam.modules.paper.service;

import com.yf.exam.core.exception.ServiceException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 *
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/1/23 15:48
 */

@Component
@ServerEndpoint("/api/socket/paper/{userId}")
@Slf4j
@EqualsAndHashCode
public class WebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final CopyOnWriteArraySet<WebSocketServer> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();
    private static final Map<String, Session> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) throws IOException {
        this.session = session;
        this.userId = userId;
        // 加入set中
        WEB_SOCKET_SET.add(this);
        SESSION_POOL.put(userId, session);
        log.info("有一个客户端连接！当前在线人数为" + SESSION_POOL.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        WEB_SOCKET_SET.remove(this);
        SESSION_POOL.remove(this.userId);
        log.info("有一个客户端连接关闭！当前在线人数为" + SESSION_POOL.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        session.getAsyncRemote().sendText("pong");
    }

    /**
     * @param session session
     * @param e       异常信息
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("发生错误");
        System.out.println(session);
        e.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public static void sendMessageToAll(String message) {
        for (WebSocketServer item : WEB_SOCKET_SET) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("推送信息内容=>{}", message);
        }
    }

    public static void sendMessageToUser(String userId, String message) {
        System.out.println("[websocket消息] 单点消息:" + message);
        Session session = SESSION_POOL.get(userId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new ServiceException("用户已离线");
        }
    }

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return WEB_SOCKET_SET;
    }

    public static Map<String, Session> getSessionPool() {
        return SESSION_POOL;
    }
}
