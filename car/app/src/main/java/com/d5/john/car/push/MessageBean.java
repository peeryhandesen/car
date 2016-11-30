package com.d5.john.car.push;

/**
 * Created by zhouyi on 2015/11/19 17:32.
 */
public class MessageBean {

    public static String ME = "ME";

    private String body;

    private String from;

    private String date;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "body='" + body + '\'' +
                ", from='" + from + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
