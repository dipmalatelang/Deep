package com.travel.cotravel.fragment.trip.module;

public class PlanTrip {
    private String location;
    private String date_from;
    private String date_to;



    public PlanTrip(String location, String date_from, String date_to) {
        this.location = location;
        this.date_from = date_from;
        this.date_to = date_to;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate_from() {
        return date_from;
    }

    public void setDate_from(String date_from) {
        this.date_from = date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public void setDate_to(String date_to) {
        this.date_to = date_to;
    }
}
