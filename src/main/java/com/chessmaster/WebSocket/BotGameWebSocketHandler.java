package com.chessmaster.WebSocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chessmaster.Models.Game;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class BotGameWebSocketHandler extends TextWebSocketHandler {
    private final BotGameSessionManager sessionManager;

    public BotGameWebSocketHandler(BotGameSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String gameId = getPathVariable(session, "gameId");
        String playerId = getPathVariable(session, "playerId");
        String uniqueId = getPathVariable(session,"uniqueId");

        // Game must already be registered
        Game game = sessionManager.getGame(gameId);
        if (game == null || !sessionManager.isGame(gameId)) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        sessionManager.addPlayer(gameId, playerId, uniqueId, session); // Handles player/spectator logic internally
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String gameId = getPathVariable(session, "gameId");
        String playerId = getPathVariable(session, "playerId");

        String payload = message.getPayload();

        for (WebSocketSession receiver : sessionManager.getReceivers(gameId, playerId)) {
            if (receiver != null && receiver.isOpen()) {
                receiver.sendMessage(new TextMessage(payload));
            }
        }

        if(sessionManager.isBotTurn(gameId)){
            sessionManager.handleBotMove(gameId);
            session.sendMessage(new TextMessage("true"));
            for (WebSocketSession receiver : sessionManager.getReceivers(gameId, playerId)) {
                if (receiver != null && receiver.isOpen()) {
                    receiver.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String gameId = getPathVariable(session, "gameId");
        String playerId = getPathVariable(session, "playerId");

        sessionManager.removePlayer(gameId, playerId); // Handles both player and spectator cleanup
    }

   private String getPathVariable(WebSocketSession session, String var) {
        String[] parts = session.getUri().getPath().split("/");

        int n = parts.length;
        if (n < 5) return "";

        switch (var) {
            case "gameId":  return parts[n-3];
            case "playerId": return parts[n-2];
            case "uniqueId": return parts[n-1];
            default: return "";
        }
    }

}
