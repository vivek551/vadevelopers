package com.vadevelopers;

public class ModelPost {

    String AutherName, content , id , published, selflink, title, updated, url;

    public ModelPost(String autherName, String content, String id, String published, String selflink, String title, String updated, String url) {
        AutherName = autherName;
        this.content = content;
        this.id = id;
        this.published = published;
        this.selflink = selflink;
        this.title = title;
        this.updated = updated;
        this.url = url;
    }

    public String getAutherName() {
        return AutherName;
    }

    public void setAutherName(String autherName) {
        AutherName = autherName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getSelflink() {
        return selflink;
    }

    public void setSelflink(String selflink) {
        this.selflink = selflink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
