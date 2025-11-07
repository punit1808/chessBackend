package com.chessmaster.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chessmaster.Models.Board;
import com.chessmaster.Models.BoolResponse;
import com.chessmaster.Models.Game;
import com.chessmaster.Models.User;
import com.chessmaster.WebSocket.GameSessionManager;
import com.chessmaster.WebSocket.BotGameSessionManager;

@Service
public class GameSessionService {
    
    @Autowired
    private GameSessionManager gameSessionManager;
    @Autowired
    private ChessService chessService;
    @Autowired
    private BotGameSessionManager botGameService;

    public Boolean addGame(String gameId,String fTurn){
        Game game=new Game(gameId,fTurn);
        game.setGameId(gameId);
        return this.gameSessionManager.registerGame(game);
    }

    public Boolean addBotGame(String gameId,String fTurn,Integer difficulty){
        Game game=new Game(gameId,fTurn,difficulty);
        game.setGameId(gameId);
        return this.botGameService.registerGame(game);
    }

    public void setWin(String gameId,BoolResponse bs){
        this.gameSessionManager.setWin(gameId,bs.getPieceColor());
    }

    public void setBotWin(String gameId,BoolResponse bs){
        this.botGameService.setWin(gameId,bs.getPieceColor());
    }

    public Boolean moveService(int or,int oc,int nr,int nc,String gameId,String uniqueId){
        Game currGame=this.gameSessionManager.getGame(gameId);
        if(currGame==null) return false;

        if(this.gameSessionManager.isSpectator(gameId,uniqueId)){
            return false;
        }
        User player1=currGame.getPlayer1Id();
        User player2=currGame.getPlayer2Id();
        if(player1.getUniqueId().equals(uniqueId) && currGame.getTurn().equals(player1.getColor())){
            Boolean ck= this.chessService.makeMove(gameId,or, oc, nr, nc, currGame.getBoard(), player1, currGame.getTurn());
            if(ck){
                String nxtTurn;
                if(player1.getColor().equals("white")){
                    nxtTurn="black";
                }else{
                    nxtTurn="white";    
                }
                currGame.setTurn(nxtTurn);
                return true;
            }
        }
        if(player2.getUniqueId().equals(uniqueId) && currGame.getTurn().equals(player2.getColor())){
            Boolean ck= this.chessService.makeMove(gameId,or, oc, nr, nc, currGame.getBoard(), player2, currGame.getTurn());
            if(ck){
                String nxtTurn;
                if(player2.getColor().equals("white")){
                    nxtTurn="black";
                }else{
                    nxtTurn="white";    
                }
                currGame.setTurn(nxtTurn);
                return true;
            }
        }
        return false;
    }

    public Boolean moveBotService(int or,int oc,int nr,int nc,String gameId,String uniqueId){
         Game currGame=this.botGameService.getGame(gameId);
        if(currGame==null) return false;

        if(this.botGameService.isSpectator(gameId,uniqueId)){
            return false;
        }
        User player1=currGame.getPlayer1Id();
        User player2=currGame.getPlayer2Id();
        if(player1.getUniqueId().equals(uniqueId) && currGame.getTurn().equals(player1.getColor())){
            Boolean ck= this.chessService.makeMove(gameId,or, oc, nr, nc, currGame.getBoard(), player1, currGame.getTurn());
            if(ck){
                String nxtTurn;
                if(player1.getColor().equals("white")){
                    nxtTurn="black";
                }else{
                    nxtTurn="white";    
                }
                currGame.setTurn(nxtTurn);
                return true;
            }
        }
        if(player2.getUniqueId().equals(uniqueId) && currGame.getTurn().equals(player2.getColor())){
            Boolean ck= this.chessService.makeMove(gameId,or, oc, nr, nc, currGame.getBoard(), player2, currGame.getTurn());
            if(ck){
                String nxtTurn;
                if(player2.getColor().equals("white")){
                    nxtTurn="black";
                }else{
                    nxtTurn="white";    
                }
                currGame.setTurn(nxtTurn);
                return true;
            }
        }
        return false;
    }

    public Board getBoard(String gameId){
        Game currGame=this.gameSessionManager.getGame(gameId);
        return currGame.getBoard();
    }

    public Board getBotBoard(String gameId){
        Game currGame=this.botGameService.getGame(gameId);
        return currGame.getBoard(); 
    }

    public String getTurn(String gameId){
        Game currGame=this.gameSessionManager.getGame(gameId);
        String turn=currGame.getTurn();

        String output;
        if(currGame.getPlayer1Id().getColor().equals(turn)){
            output=currGame.getPlayer1Id().getUniqueId();
        }
        else{
            output=currGame.getPlayer2Id().getUniqueId();
        }
        output+=" ("+turn+")";
        return output;
    }

    public String getBotTurn(String gameId){
        Game currGame=this.botGameService.getGame(gameId);
        String turn=currGame.getTurn();

        String output;
        if(currGame.getPlayer1Id().getColor().equals(turn)){
            output=currGame.getPlayer1Id().getUniqueId();
        }
        else{
            output=currGame.getPlayer2Id().getUniqueId();
        }
        output+=" ("+turn+")";
        return output;
    }



    public BoolResponse checkWin(Board board){
        Game currGame=this.gameSessionManager.getGame(board.getGameId());
        User user1,user2;
        user1=currGame.getPlayer1Id();
        user2=currGame.getPlayer2Id();
        return this.chessService.isWin(board,user1,user2);
    }
    public BoolResponse checkBotWin(Board board){
        Game currGame=this.botGameService.getGame(board.getGameId());
        User user1,user2;
        user1=currGame.getPlayer1Id();
        user2=currGame.getPlayer2Id();
        return this.chessService.isWin(board,user1,user2);
    }
}
