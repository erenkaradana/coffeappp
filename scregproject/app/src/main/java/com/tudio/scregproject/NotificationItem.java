package com.tudio.scregproject;

public class NotificationItem {
    private String title;
    private String body;
    private String imageUrl; // Kampanyalarda olabilir, normal bildirimlerde boş kalabilir

    public NotificationItem() {
    }

    public NotificationItem(String title, String body, String imageUrl) {
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
