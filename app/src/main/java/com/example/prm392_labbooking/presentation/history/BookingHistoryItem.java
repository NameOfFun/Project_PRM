package com.example.prm392_labbooking.presentation.history;

import java.util.List;

public class BookingHistoryItem {
    // Data holder representing one booking row in history screen.
    private final String id;
    private final String roomName;
    private final String date;
    private final String timeRange;
    private final String status;
    private final double price;
    private final List<String> facilities;
    private final int participants;
    private final float rating;
    private final int durationHours;

    public BookingHistoryItem(
            String id,
            String roomName,
            String date,
            String timeRange,
            String status,
            double price,
            List<String> facilities,
            int participants,
            float rating,
            int durationHours
    ) {
        this.id = id;
        this.roomName = roomName;
        this.date = date;
        this.timeRange = timeRange;
        this.status = status;
        this.price = price;
        this.facilities = facilities;
        this.participants = participants;
        this.rating = rating;
        this.durationHours = durationHours;
    }

    public String getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getDate() {
        return date;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public int getParticipants() {
        return participants;
    }

    public float getRating() {
        return rating;
    }

    public int getDurationHours() {
        return durationHours;
    }
}


