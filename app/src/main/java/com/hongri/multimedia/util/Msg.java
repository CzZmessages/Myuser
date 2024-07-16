package com.hongri.multimedia.util;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class Msg {
    public static final int TYPE_SENT = 0; // 用户发送的消息
    public static final int TYPE_RECEIVED = 1; // 接收的消息

    private String content; // 消息内容
    private String time;  //当前时间
    private int type; // 消息类型

    public Msg(String content, String time, int type) {
        this.content = content;
        this.time = time;
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
