package com.tj.mydea;

/**
 * Created by tommy on 10/11/16.
 */

@SuppressWarnings("DefaultFileTemplate")
class Idea {
    private String ideaName;
    private String description;
    private String author;
    private String date;
    private String category;
    private Integer like;
    private String author_id;
    private String comments;
    public String getideaName() {
        return ideaName;
    }
    public void setideaName(String ideaName) {
        this.ideaName = ideaName;
    }
    public String getdescription() {
        return description;
    }
    public void setdescription(String description) {
        this.description = description;
    }
    public String getauthor() {
        return author;
    }
    public void setauthor(String author) {
        this.author = author;
    }
    public String getdate() {
        return date;
    }
    public void setdate(String date) {
        this.date = date;
    }
    public Integer getlike() {
        return like;
    }
    public void setlike(Integer like) {
        this.like = like;
    }
    public String getcategory() {
        return category;
    }
    public void setcategory(String category) {
        this.category = category;
    }
    public String getauthor_id() {
        return author_id;
    }
    public void setauthor_id(String author_id) {
        this.author_id = author_id;
    }
    public String getcomments() {
        return comments;
    }
    public void setcomments(String comments) {
        this.comments = comments;
    }
}
