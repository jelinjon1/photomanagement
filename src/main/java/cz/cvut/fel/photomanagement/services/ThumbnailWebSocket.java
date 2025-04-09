package cz.cvut.fel.photomanagement.services;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
/**
 *
 * @author Jonáš
 */

@ServerEndpoint("/thumbnailUpdates")
public class ThumbnailWebSocket {

    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    public static void sendThumbnailUpdate(Long photoId, String thumbnailUrl) {
        for (Session session : sessions) {
            try {
                String message = photoId + "|" + thumbnailUrl; // Send "photoId|thumbnailUrl"
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
