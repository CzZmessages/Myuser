package com.hongri.multimedia.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author $
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class Message implements Serializable {
    private String role;
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
