package com.example.michalcop.rememberall.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Post {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String content;
    private String dateCreated;
    private String dateFor;

    public Post() {
    }

    @Ignore
    public Post(String title, String content, String dateCreated, String dateFor) {
        this.title       = title;
        this.content     = content;
        this.dateCreated = dateCreated;
        this.dateFor = dateFor;
    }

    @Ignore
    public Post(String title, String content, String dateFor){
        this.title       = title;
        this.content     = content;
        this.dateCreated = new Date().toString();
        this.dateFor = dateFor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateFor() {
        return dateFor;
    }

    public void setDateFor(String dateFor) {
        this.dateFor = dateFor;
    }
}
