package com.tudio.scregproject;


public class UserNotification {
    private String title;
    private String body;

    public UserNotification() {
        // Firestore için boş constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
