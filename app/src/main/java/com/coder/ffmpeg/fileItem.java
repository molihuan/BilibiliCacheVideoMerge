package com.coder.ffmpeg;

public class fileItem {
    private String LJ;
    private boolean icon;

    public fileItem(String LJ,boolean icon){
        this.LJ=LJ;
        this.icon=icon;
    }
    public String getLJ(){
        return LJ;
    }
    public boolean geticon(){
        return icon;
    }
    public  void setLJ(String LJ){
        this.LJ=LJ;
    }
}
