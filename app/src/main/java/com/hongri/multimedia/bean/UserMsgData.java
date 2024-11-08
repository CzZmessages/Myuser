package com.hongri.multimedia.bean;

import java.io.Serializable;

public class UserMsgData  implements Serializable {
    private int code;
    private String message;
    private UserMsgBean data;

    public UserMsgData() {
    }

    public UserMsgData(int code, String message, UserMsgBean data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserMsgBean getData() {
        return data;
    }

    public void setData(UserMsgBean data) {
        this.data = data;
    }
}
