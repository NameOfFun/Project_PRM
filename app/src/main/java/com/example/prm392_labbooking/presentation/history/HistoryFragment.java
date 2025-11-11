package com.example.prm392_labbooking.presentation.history;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_labbooking.R;
import com.example.prm392_labbooking.data.local.BookingHistoryStorage;
import com.example.prm392_labbooking.presentation.feedback.FeedbackActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_CANCELLED = "cancelled";

    // Local cache for all booking items shown in the history screen.
    private BookingHistoryAdapter adapter;
    private final List<BookingHistoryItem> allBookings = new ArrayList<>();
    private BookingHistoryStorage historyStorage;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private View emptyState;
    private TextView lastUpdatedView;
    private TextView totalSessionsView;
    private TextView totalHoursView;
    private ChipGroup filterGroup;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        progressBar = view.findViewById(R.id.progressHistory);
        recyclerView = view.findViewById(R.id.rvHistory);
        emptyState = view.findViewById(R.id.historyEmptyState);
        lastUpdatedView = view.findViewById(R.id.tvHistoryLastUpdated);
        totalSessionsView = view.findViewById(R.id.tvHistoryTotalSessionsValue);
        totalHoursView = view.findViewById(R.id.tvHistoryTotalHoursValue);
        filterGroup = view.findViewById(R.id.historyFilterGroup);
        historyStorage = BookingHistoryStorage.getInstance(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookingHistoryAdapter(this::openFeedback);
        recyclerView.setAdapter(adapter);

        MaterialButton retryButton = view.findViewById(R.id.btnHistoryRetry);
        retryButton.setOnClickListener(v -> simulateLoadData());

        setupFilters();
        simulateLoadData();

        return view;
    }

    private void setupFilters() {
        filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String filter = STATUS_COMPLETED;
            if (checkedIds.isEmpty()) {
                applyFilter(null);
                return;
            }
            int id = checkedIds.get(0);
            if (id == R.id.chipHistoryAll) {
                applyFilter(null);
            } else if (id == R.id.chipHistoryCompleted) {
                applyFilter(STATUS_COMPLETED);
            } else if (id == R.id.chipHistoryCancelled) {
                applyFilter(STATUS_CANCELLED);
            } else {
                applyFilter(null);
            }
        });
    }

    private void simulateLoadData() {
        showLoading(true);
        handler.postDelayed(() -> {
            List<BookingHistoryItem> stored = historyStorage != null
                    ? historyStorage.getHistoryItems()
                    : new ArrayList<>();
            allBookings.clear();
            allBookings.addAll(stored);
            showLoading(false);
            updateSummary(allBookings);
            adapter.submitList(new ArrayList<>(allBookings));
            applyFilter(getCurrentFilter());
        }, 600);
    }

    private void applyFilter(@Nullable String statusFilter) {
        List<BookingHistoryItem> filtered;
        if (statusFilter == null) {
            filtered = new ArrayList<>(allBookings);
        } else {
            filtered = new ArrayList<>();
            for (BookingHistoryItem item : allBookings) {
                if (statusFilter.equalsIgnoreCase(item.getStatus())) {
                    filtered.add(item);
                }
            }
        }
        adapter.submitList(filtered);
        updateEmptyState(filtered.isEmpty());
        updateSummary(filtered);
    }

    private void updateEmptyState(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void updateSummary(List<BookingHistoryItem> bookings) {
        if (bookings == null) bookings = new ArrayList<>();
        totalSessionsView.setText(String.valueOf(bookings.size()));

        int totalHours = 0;
        for (BookingHistoryItem item : bookings) {
            totalHours += Math.max(item.getDurationHours(), 0);
        }
        totalHoursView.setText(getString(R.string.history_total_hours_format, totalHours));

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
                .format(new Date());
        lastUpdatedView.setText(getString(R.string.history_last_updated, formattedDate));
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void openFeedback(BookingHistoryItem item) {
        Intent intent = new Intent(requireContext(), FeedbackActivity.class);
        intent.putExtra("room_name", item.getRoomName());
        intent.putExtra("booking_date", item.getDate());
        intent.putExtra("booking_time", item.getTimeRange());
        intent.putExtra("participants", item.getParticipants());
        ArrayList<String> facilities = item.getFacilities() != null
                ? new ArrayList<>(item.getFacilities())
                : new ArrayList<>();
        intent.putStringArrayListExtra("facilities", facilities);
        intent.putExtra("initial_rating", item.getRating());
        startActivity(intent);
    }

    @Nullable
    private String getCurrentFilter() {
        int checkedId = filterGroup.getCheckedChipId();
        if (checkedId == View.NO_ID || checkedId == R.id.chipHistoryAll) {
            return null;
        } else if (checkedId == R.id.chipHistoryCompleted) {
            return STATUS_COMPLETED;
        } else if (checkedId == R.id.chipHistoryCancelled) {
            return STATUS_CANCELLED;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        simulateLoadData();
    }
}


