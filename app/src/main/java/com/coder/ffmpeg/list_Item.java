package com.coder.ffmpeg;

public class list_Item {
    private String LJ;
    private String MC;

    public list_Item(String LJ, String MC){
        this.LJ=LJ;
        this.MC = MC;
    }
    public String getLJ(){
        return LJ;
    }
    public String getMC() {
        return MC;
    }
}
