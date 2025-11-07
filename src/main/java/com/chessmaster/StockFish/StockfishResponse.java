package com.chessmaster.StockFish;

public class StockfishResponse {
    private String bestmove;
    private Integer depth;

    public StockfishResponse() {}

    public StockfishResponse(String bestmove, Integer depth) {
        this.bestmove = bestmove;
        this.depth = depth;
    }

    public String getBestmove() {
        return bestmove;
    }

    public void setBestmove(String bestmove) {
        this.bestmove = bestmove;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
