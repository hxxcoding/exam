package com.yf.exam.modules.paper.controller;

import com.yf.exam.core.exception.ServiceException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 *  在线试卷websocket 用于监考在线试卷,发送通知等功能
 * </p>
 *
 * @author Xiaoxiao Hu
 * @since 2022/1/23 15:48
 */

@Component
@ServerEndpoint("/api/socket/paper/{paperId}")
@Slf4j
@EqualsAndHashCode
public class PaperWebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的PaperWebSocketServer对象。
     */
    private static final Set<PaperWebSocketServer> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();
    private static final Map<String, Session> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String paperId;

    /**
     * 连接建立调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("paperId") String paperId) throws IOException {
        this.session = session;
        this.paperId = paperId;
        // 加入set中
        WEB_SOCKET_SET.add(this);
        SESSION_POOL.put(paperId, session);
        log.info("试卷 paperId={} 连接！当前在线人数为" + SESSION_POOL.size(), paperId);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        WEB_SOCKET_SET.remove(this);
        SESSION_POOL.remove(this.paperId);
        log.info("试卷 paperId={} 连接关闭！当前在线人数为" + SESSION_POOL.size(), this.paperId);
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        if (message.equals("ping")) { // 心跳信号
            session.getAsyncRemote().sendText("pong");
        } else {
            log.info("收到客户端消息 => {}", message);
        }
    }

    /**
     * @param session session
     * @param e       异常信息
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("WebSocket发生错误");
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
        for (PaperWebSocketServer item : WEB_SOCKET_SET) {
            try {
                item.sendMessage(message);
            } catch (Exception e) {
                throw new ServiceException("发送失败");
            }
            log.info("发送全体消息 => {}", message);
        }
    }

    public static void sendMessageToPaper(String paperId, String message) {
        Session session = SESSION_POOL.get(paperId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                throw new ServiceException("发送失败");
            }
        } else {
            throw new ServiceException("用户已离线");
        }
        log.info("向 paperId={} 发送消息 => {}", paperId, message);
    }

    public static Map<String, Session> getSessionPool() {
        return SESSION_POOL;
    }
}
