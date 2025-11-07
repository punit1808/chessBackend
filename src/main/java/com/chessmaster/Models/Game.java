package com.chessmaster.Models;

import java.util.ArrayList;

public class Game {
    private String gameId;
    private User player1Id=null;
    private User player2Id=null;
    private ArrayList<User> spectators;
    private Board board;
    private String turn;
    private Integer difficulty;

    public Game(String gameId,String fTurn) {
        this.spectators = new ArrayList<>();
        this.board = new Board(gameId);
        if(fTurn==null || fTurn.isEmpty()){
            fTurn="white";
        }
        this.turn = fTurn ; // Default turn to white
        this.difficulty=-1;
    }
    public Game(String gameId,String fTurn,Integer difficulty) {
        this.spectators = new ArrayList<>();
        this.board = new Board(gameId);
        if(fTurn==null || fTurn.isEmpty()){
            fTurn="white";
        }
        this.turn = fTurn ; // Default turn to white
        this.difficulty=difficulty;
    }
    
    public Game(String gameId, User player1Id, User player2Id, ArrayList<User> spectators, Board board,
            String turn) {
        this.gameId = gameId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.spectators = spectators;
        this.board = board;
        this.turn = turn;
        this.difficulty=-1;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public User getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(User player1Id) {
        this.player1Id = player1Id;
    }

    public User getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(User player2Id) {
        this.player2Id = player2Id;
    }

    public ArrayList<User> getSpectators() {
        return spectators;
    }

    public void setSpectators(ArrayList<User> spectators) {
        this.spectators = spectators;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }
    
    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

}
    

