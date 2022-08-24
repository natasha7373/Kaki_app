package com.example.kakihomeui;

public class MyItems {

    private final String email, date, dateP, des, title, loc, attendees, time;

    public MyItems(String email, String date, String dateP, String des, String title, String loc, String attendees, String time) {
        this.email = email;
        this.date = date;
        this.dateP = dateP;
        this.des = des;
        this.title = title;
        this.loc = loc;
        this.attendees = attendees;
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getDateP() {
        return dateP;
    }

    public String getDes() {
        return des;
    }

    public String getTitle() {
        return title;
    }

    public String getLoc() {
        return loc;
    }

    public String getAttendees() {
        return attendees;
    }

    public String getTime() {
        return time;
    }
}
