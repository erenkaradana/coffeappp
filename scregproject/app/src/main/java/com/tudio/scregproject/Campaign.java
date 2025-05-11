package com.tudio.scregproject;


public class Campaign {
    private String title;
    private String description;
    private String imageUrl; // isteğe bağlı

    public Campaign() {
        // Boş constructor Firestore için lazım
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
