package com.example.tripify;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class admin_destinationAdapter extends RecyclerView.Adapter<admin_destinationAdapter.ViewHolder> {
    private List<Map<String, Object>> destinations;
    private List<String> destinationIds; // List to store unique IDs
    private admin_destinationAdapter.OnDestinationClickListener listener;
    private admin_destinationAdapter.showDeleteConfirmationDialogListener listener2;

    public interface OnDestinationClickListener {
        void onDestinationClick(String destinationId); // Change to pass String ID
    }
    public interface showDeleteConfirmationDialogListener {
        void showDeleteConfirmationDialog(String destinationId); // Change to pass String ID
    }

    public admin_destinationAdapter(List<Map<String, Object>> destinations, List<String> destinationIds, admin_destinationAdapter.OnDestinationClickListener listener,admin_destinationAdapter.showDeleteConfirmationDialogListener listener2) {
        this.destinations = destinations;
        this.destinationIds = destinationIds;
        this.listener = (OnDestinationClickListener) listener;
        this.listener2 = (showDeleteConfirmationDialogListener) listener2;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_admin_destination_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> destination = destinations.get(position);
        String name = (String) destination.get("name");
        String imageURL = (String) destination.get("imageURL");

        holder.nameTextView.setText(name);
        if (imageURL != null) {
            Picasso.get().load(imageURL).into(holder.desimageis);
        } else {
            holder.desimageis.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.editButton.setOnClickListener(view -> {

                if (listener != null && destinationIds.size() > position) {
                    listener.onDestinationClick(destinationIds.get(position)); // Pass the ID on click
                }



        });
        holder.deleteButton.setOnClickListener(view -> {
            if (listener2 != null && destinationIds.size() > position) {
                listener2.showDeleteConfirmationDialog(destinationIds.get(position)); // Pass the ID on click
            }        });
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageButton editButton, deleteButton;
        ImageView desimageis;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_destination_name);
            editButton = itemView.findViewById(R.id.button_edit_destination);
            deleteButton = itemView.findViewById(R.id.button_delete_destination);
            desimageis = itemView.findViewById(R.id.imageView11);

        }
    }



}

