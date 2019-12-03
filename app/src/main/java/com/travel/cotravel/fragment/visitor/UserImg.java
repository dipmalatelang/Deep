package com.travel.cotravel.fragment.visitor;

import com.travel.cotravel.fragment.trip.module.User;

import java.io.Serializable;

public class UserImg implements Serializable {
    User user;
    String pictureUrl;
    int fav;

    public UserImg(User user, String pictureUrl, int fav) {
        this.user=user;
        this.pictureUrl=pictureUrl;
        this.fav=fav;
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
