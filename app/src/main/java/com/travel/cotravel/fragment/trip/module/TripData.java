package com.travel.cotravel.fragment.trip.module;

public class TripData {

    private String id;
    private String location;
    private String trip_note;
    private String from_date;
    private String to_date;


    public TripData() {

    }

    public TripData(String id, String location, String trip_note, String from_date, String to_date) {
        this.id=id;
        this.location = location;
        this.trip_note = trip_note;
        this.from_date = from_date;
        this.to_date = to_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTrip_note() {
        return trip_note;
    }

    public void setTrip_note(String trip_note) {
        this.trip_note = trip_note;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

}
