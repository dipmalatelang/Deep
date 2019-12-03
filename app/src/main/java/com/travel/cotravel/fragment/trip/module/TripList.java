package com.travel.cotravel.fragment.trip.module;

import com.travel.cotravel.fragment.visitor.UserImg;

import java.io.Serializable;

public class TripList implements Serializable {

    private int visit_id;
/*    private int favid;
    private String id;
    private String name;
    private String about_me;
    private String imageUrl;*/
    private String planLocation;
    private String trip_note;
    private String from_to_date;
 /*   private String userLocation;
    private String gender,age;
    private String nationality, lang, height,  body_type,  eyes,  hair, visit;
    private ArrayList<String> travel_with, looking_for;
    private int account_type;
    private String phone;*/

    User user;
    UserImg userImg;


    public TripList(User user, UserImg userImg,  String planLocation, String trip_note, String from_to_date, int visit_id) {
        this.user=user;
        this.userImg=userImg;
        this.planLocation=planLocation;
        this.trip_note=trip_note;
        this.from_to_date=from_to_date;
        this.visit_id=visit_id;
    }

    public TripList(User user, UserImg userImg, String trip_note, int visit_id) {
        this.user=user;
        this.userImg=userImg;
        this.trip_note=trip_note;
        this.visit_id=visit_id;
    }

    public int getVisit_id() {
        return visit_id;
    }

    public String getPlanLocation() {
        return planLocation;
    }

    public String getTrip_note() {
        return trip_note;
    }

    public String getFrom_to_date() {
        return from_to_date;
    }

    public User getUser() {
        return user;
    }

    public UserImg getUserImg() {
        return userImg;
    }

    public void setVisit_id(int visit_id) {
        this.visit_id = visit_id;
    }

    public void setPlanLocation(String planLocation) {
        this.planLocation = planLocation;
    }

    public void setTrip_note(String trip_note) {
        this.trip_note = trip_note;
    }

    public void setFrom_to_date(String from_to_date) {
        this.from_to_date = from_to_date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserImg(UserImg userImg) {
        this.userImg = userImg;
    }

    /*

    public TripList(String id, String name, String imageUrl, String phone, String age, String gender, String about_me, String userLocation, String nationality, String lang, String height, String body_type, String eyes, String hair, ArrayList<String> looking_for, ArrayList<String> travel_with, String visit, String planLocation, String trip_note, String from_to_date, int account_type, int favid, int visit_id) {
        this.id=id;
        this.name=name;
        this.imageUrl=imageUrl;
        this.phone=phone;
        this.planLocation = planLocation;
        this.trip_note = trip_note;
        this.from_to_date = from_to_date;
        this.age=age;
        this.gender=gender;
        this.about_me=about_me;
        this.userLocation=userLocation;
        this.nationality=nationality;
        this.lang= lang;
        this.height=height;
        this.body_type= body_type;
        this.eyes=eyes;
        this.hair=hair;
        this.looking_for=looking_for;
        this.travel_with=travel_with;
        this.visit= visit;
        this.account_type=account_type;
        this.favid=favid;
        this.visit_id=visit_id;
    }

    public TripList(String id, String username, String imageURL, String phone, String age,String gender, String about_me, String location, String nationality, String lang, String height, String body_type, String eyes, String hair, ArrayList<String> looking_for, ArrayList<String> travel_with, String visit, String tripNote, int account_type, int fav_id, int visit_id) {
        this.id=id;
        this.name=username;
        this.imageUrl=imageURL;
        this.phone=phone;
        this.trip_note = tripNote;
        this.age=age;
        this.gender=gender;
        this.about_me=about_me;
        this.userLocation=location;
        this.nationality=nationality;
        this.lang= lang;
        this.height=height;
        this.body_type= body_type;
        this.eyes=eyes;
        this.hair=hair;
        this.looking_for=looking_for;
        this.travel_with=travel_with;
        this.visit= visit;
        this.account_type=account_type;
        this.favid=fav_id;
        this.visit_id=visit_id;
    }
*/

   /* public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserImg getUserImg() {
        return userImg;
    }

    public void setUserImg(UserImg userImg) {
        this.userImg = userImg;
    }

    public ArrayList<String> getLooking_for() {
        return looking_for;
    }

    public void setLooking_for(ArrayList<String> looking_for) {
        this.looking_for = looking_for;
    }

    public int getAccount_type() {
        return account_type;
    }


    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public ArrayList<String> getTravel_with() {
        return travel_with;
    }

    public void setTravel_with(ArrayList<String> travel_with) {
        this.travel_with = travel_with;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public int getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(int visit_id) {
        this.visit_id = visit_id;
    }

    public int getFavid() {
        return favid;
    }

    public void setFavid(int favid) {
        this.favid = favid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public String getEyes() {
        return eyes;
    }

    public void setEyes(String eyes) {
        this.eyes = eyes;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(String hair) {
        this.hair = hair;
    }
    

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPlanLocation() {
        return planLocation;
    }

    public void setPlanLocation(String planLocation) {
        this.planLocation = planLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrip_note() {
        return trip_note;
    }

    public void setTrip_note(String trip_note) {
        this.trip_note = trip_note;
    }

    public String getFrom_to_date() {
        return from_to_date;
    }

    public void setFrom_to_date(String from_to_date) {
        this.from_to_date = from_to_date;
    }*/
}
