package com.travel.cotravel.fragment.chat.module;

public class Chat {

    private String sender;
    private String receiver;
    private String message, msg_date, msg_time;
    private boolean isseen;

    public Chat(String sender, String receiver, String message, String msg_date, String msg_time, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.msg_date=msg_date;
        this.msg_time=msg_time;
        this.isseen = isseen;

    }

    public Chat() {
    }

    public String getMsg_date() {
        return msg_date;
    }

    public void setMsg_date(String msg_date) {
        this.msg_date = msg_date;
    }

    public String getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
