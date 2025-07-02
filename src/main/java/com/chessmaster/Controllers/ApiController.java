package com.chessmaster.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.chessmaster.Models.Board;
import com.chessmaster.Models.BoolResponse;
import com.chessmaster.Models.GameData;
import com.chessmaster.Models.MoveResponse;
import com.chessmaster.Models.SocketResponse;
import com.chessmaster.Repo.GameEntityRepo;
import com.chessmaster.Service.ChessService;
import com.chessmaster.Service.GameSessionService;


@RestController
@CrossOrigin(
    origins = {
        "https://chess-frontend-git-main-punit1808s-projects.vercel.app",
        "https://chess-frontend-punit1808s-projects.vercel.app",
        "https://chess-frontend-ashy.vercel.app"})

public class ApiController {
    
    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private ChessService chessService;

    @Autowired
    private GameEntityRepo repo;
    
    @GetMapping("/api/game/create")
    public ResponseEntity<?> createGame() {
        String gameId = UUID.randomUUID().toString().substring(0, 6);
        Map<String, String> response = new HashMap<>();
        response.put("gameId", gameId);
        Boolean ck=this.gameSessionService.addGame(gameId);
        if(ck){
            return ResponseEntity.ok(response);}
        
        return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error occured.");
                    
    }

    
    @GetMapping("/api/game/join/{gameId}")
    public ResponseEntity<String> joinGame(@PathVariable String gameId) {
        String UniqueId=UUID.randomUUID().toString().substring(0, 6);
        return ResponseEntity.ok(UniqueId);
    }

    @GetMapping("/api/game/move/{or}/{oc}/{nr}/{nc}/{gameId}/{uniqueId}")
    public MoveResponse makeMove( @PathVariable int or, @PathVariable int oc,@PathVariable int nr, @PathVariable int nc,@PathVariable String gameId,@PathVariable String uniqueId){  
        
        Boolean validMove= this.gameSessionService.moveService(or,oc,nr,nc,gameId,uniqueId); 
        BoolResponse bs1=this.chessService.isSafe(getCurrentState(gameId).getBoard());
        BoolResponse bs2=this.gameSessionService.checkWin(getCurrentState(gameId));
        
        if(bs2.getRes()){
            this.gameSessionService.setWin(gameId,bs2);
        }
        MoveResponse mr=new MoveResponse(validMove,bs1,bs2);
        return mr;
    }

    @GetMapping("/api/game/board/{gameId}")
    public Board getCurrentState(@PathVariable String gameId) {
        return this.gameSessionService.getBoard(gameId);
    }

    @GetMapping("/api/game/rerender/{gameId}")
    public SocketResponse rerender(@PathVariable String gameId){
        Board board=this.gameSessionService.getBoard(gameId);
        String turn=turn(gameId);
        BoolResponse isSafe=this.chessService.isSafe(getCurrentState(gameId).getBoard());
        BoolResponse isWin=this.gameSessionService.checkWin(getCurrentState(gameId));

        SocketResponse sr=new SocketResponse(board, turn, isSafe, isWin);
        return sr;
    }

    @GetMapping("/api/game/turn/{gameId}")
    public String turn(@PathVariable String gameId){
        GameData gd = repo.findByGameId(gameId)
            .orElseThrow(() -> new RuntimeException("Game not found"));

        String turn=this.gameSessionService.getTurn(gameId);
        if(turn.equals("white")){
            return gd.getPlayer1Id();
        }
        else{
            return gd.getPlayer2Id();
        }
    }

    @GetMapping("/api/game/isSafe/{gameId}")
    public ResponseEntity<BoolResponse> isSafe(@PathVariable String gameId){
        BoolResponse res=this.chessService.isSafe(getCurrentState(gameId).getBoard());

        if(res.getRes()){
            return ResponseEntity.ok(res);
        }
        else{
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/game/Win/{gameId}")
    public ResponseEntity<BoolResponse> isWin(@PathVariable String gameId){
        BoolResponse res=this.gameSessionService.checkWin(getCurrentState(gameId));

        if(res.getRes()){
            return ResponseEntity.ok(res);
         }
         else{
           return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);}
    }
}
