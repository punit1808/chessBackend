package com.chessmaster.WebSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.chessmaster.Models.Game;
import com.chessmaster.Models.GameData;
import com.chessmaster.Models.Move;
import com.chessmaster.Models.Piece;
import com.chessmaster.Models.User;
import com.chessmaster.Repo.GameEntityRepo;
import com.chessmaster.Repo.UserRepo;
import com.chessmaster.Service.ChessService;
import com.chessmaster.StockFish.FenUtil;
import com.chessmaster.Models.BoolResponse;

@Component
public class BotGameSessionManager {
     
    @Autowired
    private GameEntityRepo gameRepo;

    @Autowired ChessService chessService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FenUtil fenUtil;

    // gameId -> Game object created externally (controller)
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    // playerId or spectatorId -> WebSocketSession for all connected users
    private final Map<User, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public Boolean registerGame(Game game) {
        if (game != null && game.getGameId() != null) {
            games.put(game.getGameId(), game);
            GameData gd=new GameData();
            gd.setGameId(game.getGameId());
            this.gameRepo.save(gd);
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean isBotTurn(String gameId){
        Game game = games.get(gameId);
        if(game==null) return false;
        User bot=game.getPlayer2Id();
        if(bot==null) return false;
        if(game.getTurn().equals(bot.getColor())){
            return true;
        }
        return false;
    }

    public void handleBotMove(String gameId){
    Game game = games.get(gameId);
    if (game == null) return;
    User player = game.getPlayer1Id();
    User bot = game.getPlayer2Id();
    if (bot == null) return;

    Piece[][] gameBoard = game.getBoard().getBoard();
    String turn = game.getTurn();

    String fen = FenUtil.boardToFEN(gameBoard, turn);

    Move botMove = fenUtil.getnxtMove(fen, game.getDifficulty());

    if (botMove == null) {
        return;
    }

    Boolean ck = chessService.makeMove(
        game.getGameId(),
        botMove.getOr(), botMove.getOc(),
        botMove.getNr(), botMove.getNc(),
        game.getBoard(), bot, game.getTurn()
    );

    if (ck) {
        String nextTurn = bot.getColor().equals("white") ? "black" : "white";
        BoolResponse bs = chessService.isWin(game.getBoard(),player,bot);
        if (bs.getRes()) {
            setWin(gameId, bot.getColor());
        }
        game.setTurn(nextTurn);
    }
    else{
        handleBotMove(gameId);
    }
}

    public void setWin(String gameId,String color){
        GameData gd = gameRepo.findByGameId(gameId)
            .orElseThrow(() -> new RuntimeException("Game not found"));
        gd.setWinner(color);
        gameRepo.save(gd);
    }

    public Boolean isGame(String gameId){
        Game game =games.get(gameId);
        if(game==null){
            return false;
        }
        return true;
    }

    public boolean addPlayer(String gameId, String playerId, String uniqueId, WebSocketSession session) {
        Game game = games.get(gameId);
        if (game == null) {
            // Game not found — must be created first in controller
            return false;
        }

        synchronized (game) {
            User p1=game.getPlayer1Id();
            
            GameData gd = gameRepo.findByGameId(gameId)
            .orElseThrow(() -> new RuntimeException("Game not found"));
            User user =new User(gameId,playerId,uniqueId);
            User user1 =new User(gameId,playerId,uniqueId);
            User user2= new User(gameId,"bot","bot-"+gameId);
            if (p1 == null) {
                user1.setType("player");
                user1.setColor(game.getTurn());    

                gd.setPlayer1Id(playerId);
                gameRepo.save(gd);
                userRepo.save(user1);
                game.setPlayer1Id(user1);
                sessions.put(user1, session);
            
                String turn;
                if(game.getTurn().equals("white")){
                    turn="black";
                }
                else{
                    turn="white";
                }
                user2.setType("bot");
                user2.setColor(turn);
                game.setPlayer2Id(user2);
                gd.setPlayer2Id("bot");
                userRepo.save(user2);
                gameRepo.save(gd);

                return true;
            } 
            else {
                
                // Add as spectator if not already present
                ArrayList<User> specs=game.getSpectators();
                
                for(User i:specs){
                    if(i.getUserId().equals(playerId)){
                        return false;
                    }
                }
                
                user.setType("spectator");
                specs.add(user);
                game.setSpectators(specs);
                userRepo.save(user);
                sessions.put(user, session);
                gameRepo.save(gd);
                return true;
            }  
        }
    }

    public boolean isPlayer(String gameId, String playerId) {
        Game game = games.get(gameId);
        if (game == null) return false;
        return playerId.equals(game.getPlayer1Id().getUniqueId()) || playerId.equals(game.getPlayer2Id().getUniqueId());
    }

    public boolean isSpectator(String gameId, String playerId) {
        Game game = games.get(gameId);
        if (game == null) return false;
        ArrayList<User> specs=game.getSpectators();
        for(User i:specs){
            if(i.getUniqueId().equals(playerId)){
                return true;
            }
        }
        return false;
    }

    public boolean isTwoPlayersLocked(String gameId) {
        Game game = games.get(gameId);
        if (game == null) return false;
        return game.getPlayer1Id() != null && game.getPlayer2Id() != null;
    }

    public void removePlayer(String gameId, String playerId) {
    Game game = games.get(gameId);
    if (game == null) return;

    synchronized (game) {

        User p1 = game.getPlayer1Id();
        User p2 = game.getPlayer2Id();

        if (p1 != null && p1.getUserId().equals(playerId)) {
            game.setPlayer1Id(null);
            sessions.remove(p1);
        } else if (p2 != null && p2.getUserId().equals(playerId)) {
            game.setPlayer2Id(null);
            sessions.remove(p2);
        } else {
            ArrayList<User> specs = game.getSpectators();
            User toRemove = null;
            for (User u : specs) {
                if (u.getUserId().equals(playerId)) {
                    toRemove = u;
                    break;
                }
            }
            if (toRemove != null) {
                specs.remove(toRemove);
                game.setSpectators(specs);
                sessions.remove(toRemove);
            }
        }

        cleanupGameIfEmpty(gameId);
    }
}

    private void cleanupGameIfEmpty(String gameId) {
        Game game = games.get(gameId);
        if (game == null) return;
        if (game.getPlayer1Id() == null && game.getPlayer2Id() == null && game.getSpectators().isEmpty()) {
            games.remove(gameId);
        }
    }

    public List<WebSocketSession> getReceivers(String gameId, String senderId) {
        List<WebSocketSession> receivers = new ArrayList<>();
        Game game = games.get(gameId);
        if (game == null) return receivers;

        if (isPlayer(gameId, senderId)) {
            // Add the other player
            User player1=game.getPlayer1Id();
            User player2=game.getPlayer2Id();

            if( player1!=null && player2!=null &&player1.getUserId().equals(senderId)){
                receivers.add(sessions.get(player2));
            }
            else{
                if(player1!=null && player2!=null){
                    receivers.add(sessions.get(player1));
                }
            }

            // Add all spectators
            for (User spectatorId : game.getSpectators()) {
                WebSocketSession specSession = sessions.get(spectatorId);
                if (specSession != null) receivers.add(specSession);
            }
        }
        return receivers;
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }
}
