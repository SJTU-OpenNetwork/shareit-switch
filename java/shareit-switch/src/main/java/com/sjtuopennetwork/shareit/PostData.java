package com.sjtuopennetwork.shareit;

public class PostData {
    public String cmdType;
    public String cmd;

    @Override
    public String toString(){
        return "[cmdType="+cmdType+",cmd="+cmd+"]";
    }

}