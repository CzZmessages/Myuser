package com.hongri.multimedia.bean;

public class TextSendBean {
    private String message;
    private String chat_id;

    public TextSendBean(String message, String chat_id) {
        this.message = message;
        this.chat_id = chat_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }
}
