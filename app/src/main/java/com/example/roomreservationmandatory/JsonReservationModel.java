package com.example.roomreservationmandatory;

import java.io.Serializable;
import java.util.Date;

public class JsonReservationModel implements Serializable {
    /**
     * id : 0
     * fromTime : 0
     * toTime : 0
     * userId : string
     * purpose : string
     * roomId : 0
     */

    private int id;
    private int fromTime;
    private int toTime;
    private String userId;
    private String purpose;
    private int roomId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromTime() {
        Date date = new Date();
        date.setTime((long) fromTime * 1000);
        return date.toString();
    }

    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        Date date = new Date();
        date.setTime((long) fromTime * 1000);
        return date.toString();
    }

    public void setToTime(int toTime) {
        this.toTime = toTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Reservation: " + "\n" +
                "Fra: " + getFromTime() + "\n" +
                "Til: " + getToTime() + "\n" +
                "Af: " + userId + "\n" +
                "Formål: " + purpose;
    }
}