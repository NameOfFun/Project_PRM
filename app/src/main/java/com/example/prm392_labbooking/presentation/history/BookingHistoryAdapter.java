package com.example.prm392_labbooking.presentation.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_labbooking.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.HistoryViewHolder> {

    // Backing list for items displayed in booking history recycler view.
    private final List<BookingHistoryItem> data = new ArrayList<>();
    private final HistoryActionListener actionListener;

    public interface HistoryActionListener {
        void onFeedbackClick(BookingHistoryItem item);
    }

    public BookingHistoryAdapter(HistoryActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        BookingHistoryItem item = data.get(position);
        holder.bind(item, actionListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void submitList(List<BookingHistoryItem> bookings) {
        data.clear();
        if (bookings != null) {
            data.addAll(bookings);
        }
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomName;
        private final TextView date;
        private final TextView time;
        private final TextView status;
        private final TextView price;
        private final TextView facilities;
        private final TextView participants;
        private final TextView rating;
        private final MaterialButton feedbackButton;
        private final ImageView statusIcon;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tvHistoryItemRoom);
            date = itemView.findViewById(R.id.tvHistoryItemDate);
            time = itemView.findViewById(R.id.tvHistoryItemTime);
            status = itemView.findViewById(R.id.tvHistoryItemStatus);
            price = itemView.findViewById(R.id.tvHistoryItemPrice);
            facilities = itemView.findViewById(R.id.tvHistoryItemFacilities);
            participants = itemView.findViewById(R.id.tvHistoryItemParticipants);
            rating = itemView.findViewById(R.id.tvHistoryItemRating);
            feedbackButton = itemView.findViewById(R.id.btnHistoryItemFeedback);
            statusIcon = itemView.findViewById(R.id.ivHistoryItemStatusIcon);
        }

        void bind(BookingHistoryItem item, HistoryActionListener actionListener) {
            roomName.setText(item.getRoomName());
            date.setText(item.getDate());
            time.setText(item.getTimeRange());
            price.setText(itemView.getContext().getString(R.string.history_item_price_format, item.getPrice()));
            participants.setText(itemView.getContext().getString(R.string.feedback_participants_format, item.getParticipants()));
            rating.setText(itemView.getContext().getString(R.string.history_item_rating_format, item.getRating()));

            if (item.getFacilities() != null && !item.getFacilities().isEmpty()) {
                facilities.setVisibility(View.VISIBLE);
                facilities.setText(itemView.getContext().getString(
                        R.string.feedback_facilities_format,
                        android.text.TextUtils.join(" • ", item.getFacilities())
                ));
            } else {
                facilities.setVisibility(View.GONE);
            }

            int statusColor;
            int statusIconRes;
            if ("completed".equalsIgnoreCase(item.getStatus())) {
                status.setText(R.string.history_status_completed);
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorSecondary);
                statusIconRes = R.drawable.ic_history_success;
            } else if ("cancelled".equalsIgnoreCase(item.getStatus())) {
                status.setText(R.string.history_status_cancelled);
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorError);
                statusIconRes = R.drawable.ic_history_cancelled;
            } else {
                status.setText(item.getStatus());
                statusColor = ContextCompat.getColor(itemView.getContext(), R.color.colorOnSurface);
                statusIconRes = R.drawable.ic_history_pending;
            }
            status.setTextColor(statusColor);
            statusIcon.setColorFilter(statusColor);
            statusIcon.setImageResource(statusIconRes);

            feedbackButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onFeedbackClick(item);
                }
            });
        }
    }
}


