package com.example.tripify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private List<Map<String, Object>> tickets;
    private List<String> ticketIds;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(String ticketId);

            void onTicketDelete(String ticketId); // this line to handle deletion

    }

    public TicketAdapter(List<Map<String, Object>> tickets, List<String> ticketIds, OnTicketClickListener listener) {
        this.tickets = tickets;
        this.ticketIds = ticketIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_ticket_adapter, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> ticket = tickets.get(position);
        String ticketId = ticketIds.get(position);
        holder.bind(ticket, ticketId);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, dateOfPurchase, ticketCount, totalPrice, ticketId;
        ImageButton deletebtn;
        public ViewHolder(View itemView, OnTicketClickListener listener) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            dateOfPurchase = itemView.findViewById(R.id.dateOfPurchase);
            ticketCount = itemView.findViewById(R.id.ticketCount);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            ticketId = itemView.findViewById(R.id.ticketId);
            deletebtn = itemView.findViewById(R.id.imagebtndelete);


            deletebtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTicketDelete(ticketIds.get(position)); // Trigger the deletion
                }
            });

            // Remove onClickListener from itemView to prevent whole view from triggering delete
            // Optionally, add another listener here if you need to handle clicks on the entire item differently

        }

        void bind(Map<String, Object> ticket, String ticketId) {
            userEmail.setText(String.format("Email: %s", ticket.get("userEmail").toString()));
            dateOfPurchase.setText(String.format("Date: %s", ticket.get("date").toString()));
            ticketCount.setText(String.format("Tickets: %s", ticket.get("ticket").toString()));
            totalPrice.setText(String.format("Total Price: $%.2f", Double.parseDouble(ticket.get("totalPrice").toString())));
            this.ticketId.setText(String.format("Ticket ID: %s", ticketId));


        }
    }
}
