package com.chessmaster.Models;

public class Move {
        public int getOr() {
        return or;
    }

    public void setOr(int or) {
        this.or = or;
    }

    public int getOc() {
        return oc;
    }

    public void setOc(int oc) {
        this.oc = oc;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public int getNc() {
        return nc;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

        public int or, oc, nr, nc;

        public Move(){
            
        }

        public Move(int or, int oc, int nr, int nc) {
            this.or = or;
            this.oc = oc;
            this.nr = nr;
            this.nc = nc;
        }
}
