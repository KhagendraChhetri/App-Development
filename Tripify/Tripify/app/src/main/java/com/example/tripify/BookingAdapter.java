package com.example.tripify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Map<String, Object>> bookings;
    private List<String> bookingIds;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(String bookingId);
    }

    public BookingAdapter(List<Map<String, Object>> bookings, List<String> bookingIds, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.bookingIds = bookingIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_booking_adapter, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> booking = bookings.get(position);
        String bookingId = bookingIds.get(position);
        holder.bind(booking, bookingId);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bookingDate, bookingDestination, bookingTickets, bookingPrice;
        Button viewTicketButton;

        public ViewHolder(View itemView, OnBookingClickListener listener) {
            super(itemView);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingDestination = itemView.findViewById(R.id.bookingDestination);
            bookingTickets = itemView.findViewById(R.id.bookingTickets);
            bookingPrice = itemView.findViewById(R.id.bookingPrice);
            viewTicketButton = itemView.findViewById(R.id.ViewTicket);

            viewTicketButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookingClick(bookingIds.get(position));
                }
            });

        }

        void bind(Map<String, Object> booking, String bookingId) {
            String date = (String) booking.get("date");
            String destinationName = (String) booking.get("destinationName");
            Number ticketCount = (Number) booking.get("ticket");
            Number totalPrice = (Number) booking.get("totalPrice");

            bookingDate.setText("Date: " + date);
            bookingDestination.setText("Destination: " + destinationName);
            bookingTickets.setText("Tickets: " + ticketCount.toString());
            bookingPrice.setText("Total Price: $" + String.format("%.2f", totalPrice.doubleValue()));
        }
    }
}
