package com.example.prm392_labbooking.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.prm392_labbooking.presentation.history.BookingHistoryItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Đơn giản lưu trữ lịch sử đặt lab bằng SharedPreferences (JSON).
 * Giữ logic nội bộ để chuyển đổi sang {@link BookingHistoryItem} khi màn History cần dùng.
 */
public class BookingHistoryStorage {
    // Singleton helper dedicated to persisting booking history entries.

    private static final String PREF_NAME = "booking_history_prefs";
    private static final String KEY_HISTORY = "booking_history_records";

    private static BookingHistoryStorage instance;

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    private BookingHistoryStorage(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized BookingHistoryStorage getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new BookingHistoryStorage(context.getApplicationContext());
        }
        return instance;
    }

    public synchronized void addRecords(@NonNull List<BookingHistoryRecord> records) {
        if (records.isEmpty()) return;
        List<BookingHistoryRecord> current = loadRecordsInternal();
        current.addAll(records);
        Collections.sort(current, Comparator.comparingLong(BookingHistoryRecord::getCreatedAt).reversed());
        saveRecordsInternal(current);
    }

    public synchronized void addRecord(@NonNull BookingHistoryRecord record) {
        List<BookingHistoryRecord> current = loadRecordsInternal();
        current.add(record);
        Collections.sort(current, Comparator.comparingLong(BookingHistoryRecord::getCreatedAt).reversed());
        saveRecordsInternal(current);
    }

    public synchronized List<BookingHistoryItem> getHistoryItems() {
        List<BookingHistoryRecord> records = loadRecordsInternal();
        List<BookingHistoryItem> items = new ArrayList<>();
        for (BookingHistoryRecord record : records) {
            items.add(record.toBookingHistoryItem());
        }
        return items;
    }

    public synchronized void clear() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    private void saveRecordsInternal(List<BookingHistoryRecord> records) {
        String json = gson.toJson(records);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }

    private List<BookingHistoryRecord> loadRecordsInternal() {
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<BookingHistoryRecord>>() {}.getType();
        List<BookingHistoryRecord> records = gson.fromJson(json, type);
        return records != null ? new ArrayList<>(records) : new ArrayList<>();
    }

    /**
     * Dữ liệu thô lưu trong SharedPreferences. Có constructor rỗng để Gson deserialize.
     */
    public static class BookingHistoryRecord {
        private String id;
        private String roomName;
        private String date;
        private String timeRange;
        private String status;
        private double price;
        private List<String> facilities;
        private int participants;
        private float rating;
        private int durationHours;
        private long createdAt;

        public BookingHistoryRecord() {
            // No-op for Gson
        }

        public BookingHistoryRecord(
                String roomName,
                String date,
                String timeRange,
                double price,
                List<String> facilities,
                int participants,
                int durationHours
        ) {
            this.id = UUID.randomUUID().toString();
            this.roomName = roomName;
            this.date = date;
            this.timeRange = timeRange;
            this.status = "completed";
            this.price = price;
            this.facilities = facilities != null ? new ArrayList<>(facilities) : new ArrayList<>();
            this.participants = participants;
            this.rating = 0f;
            this.durationHours = durationHours;
            this.createdAt = System.currentTimeMillis();
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public BookingHistoryItem toBookingHistoryItem() {
            return new BookingHistoryItem(
                    id,
                    roomName,
                    date,
                    timeRange,
                    status,
                    price,
                    facilities,
                    participants,
                    rating,
                    durationHours
            );
        }
    }
}

