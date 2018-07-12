package com.example.asus.howdy;

public class Messages {

    private String message, type;
    private long  time;
    private boolean seen;
    private String from;


    public Messages(){

    }

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() { return from; }

    public void setFrom(String from) { this.from = from; }


    public Messages(String message, boolean seen, long time, String type) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;

    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public  boolean isSeen(){ return seen; }

    public void setSeen(boolean seen){ this.seen=seen; }

    public long getTime() { return time; }

    public void setTime(long time) { this.time = time; }




}
