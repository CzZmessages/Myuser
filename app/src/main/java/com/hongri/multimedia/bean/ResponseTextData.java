package com.hongri.multimedia.bean;

import java.io.Serializable;

/**
 * @author $
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class ResponseTextData  implements Serializable {


    private String model;


    private String createdAt;


    private Message message;


    private boolean done;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public static class Message {

        private String role;


        private String content;

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

}
