package com.example.neredeyimapp.Model;

import com.google.type.Date;
public class Post {
    private String userImage;
    private String username;
    private String postImage;
    private String description;
    private int likeCount;
    private String timestamp; // Postun paylaşım tarih ve saati
    public Post() {
    }
    public Post(String userImage, String username, String postImage, String description, int likeCount, String timestamp) {
        this.userImage = userImage;
        this.username = username;
        this.postImage = postImage;
        this.description = description;
        this.likeCount = likeCount;
        this.timestamp = timestamp;
    }
    // Getters setters
    public String getUserImage() {
        return userImage;
    }
    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPostImage() {
        return postImage;
    }
    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
