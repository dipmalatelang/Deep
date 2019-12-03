package com.travel.cotravel.fragment.chat.module;

import com.travel.cotravel.fragment.trip.module.User;

import java.io.Serializable;

public class UserImgMsg implements Serializable {
    User user;
    String pictureUrl;
    int fav;
   String lastMsg,lastMsgTime;
   boolean textType;


    public UserImgMsg(User user, String pictureUrl, int fav, String lastMsg, String lastMsgTime, boolean textType) {
        this.user = user;
        this.pictureUrl = pictureUrl;
        this.fav = fav;
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.textType = textType;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public boolean isTextType() {
        return textType;
    }

    public void setTextType(boolean textType) {
        this.textType = textType;
    }

    public int getFav() {
        return fav;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
