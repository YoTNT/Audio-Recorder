package com.codepath.audiorecorder;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_SIZE = "size";
    public static final String KEY_DURATION = "duration";

    public ParseFile getAudio(){
        return getParseFile(KEY_AUDIO);
    }

    public void setAudio(ParseFile parseFile){
        put(KEY_AUDIO,parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER,parseUser);
    }

    public String getFileSize(){
        return getString(KEY_SIZE);
    }

    public void setFileSize(int size){
        put(KEY_SIZE, size);
    }

    public String getDuration(){
        return getString(KEY_DURATION);
    }

    public void setDuration(String duration){
        put(KEY_DURATION,duration);
    }
}
