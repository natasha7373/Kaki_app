package com.example.kakihomeui;

import com.firebase.client.ServerValue;

public class Comment {

    private String text, email1;
    private Object timestamp;

    public Comment() {

    }

    public Comment(String text, String email1) {
        this.text = text;
        this.email1 = email1;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Comment(String text, String email1, Object timestamp) {
        this.text = text;
        this.email1 = email1;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail(String email1) {
        this.email1 = email1;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
