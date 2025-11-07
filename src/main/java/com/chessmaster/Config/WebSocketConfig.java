package com.chessmaster.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.chessmaster.WebSocket.BotGameWebSocketHandler;
import com.chessmaster.WebSocket.GameWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameWebSocketHandler handler;
    private final BotGameWebSocketHandler botHandler;

    public WebSocketConfig(GameWebSocketHandler handler,BotGameWebSocketHandler botHandler) {
        this.handler = handler;
        this.botHandler = botHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/wss/game/{gameId}/{playerId}/{uniqueId}").setAllowedOrigins("*");

        registry.addHandler(botHandler, "/wss/game/bot/{gameId}/{playerId}/{uniqueId}").setAllowedOrigins("*");
    }
    
}
