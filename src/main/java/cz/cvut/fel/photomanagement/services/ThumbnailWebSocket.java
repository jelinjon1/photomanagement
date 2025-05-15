package cz.cvut.fel.photomanagement.services;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implements a websocket endpoint that allows sending a message to client from server on thumbnail generation.
 *
 * @author jelinjon
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
                String message = photoId + "|" + thumbnailUrl; // message format: photoId|thumbnailUrl
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
