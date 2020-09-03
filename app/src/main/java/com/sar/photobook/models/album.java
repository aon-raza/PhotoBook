package com.sar.photobook.models;

public class album {
    private String title;
    private String photoUrl;

    public album() {
    }

    public album(String title, String photoUrl) {
        this.title = title;
        this.photoUrl = photoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
