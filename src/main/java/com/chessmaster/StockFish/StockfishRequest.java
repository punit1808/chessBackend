package com.chessmaster.StockFish;

public class StockfishRequest {
    private String fen;
    private Integer depth;

    public StockfishRequest() {}

    public StockfishRequest(String fen, Integer depth) {
        this.fen = fen;
        this.depth = depth;
    }

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
