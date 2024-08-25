package com.example.tripify;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private List<Map<String, Object>> destinations;
    private List<String> destinationIds; // List to store unique IDs
    private OnDestinationClickListener listener;

    public interface OnDestinationClickListener {
        void onDestinationClick(String destinationId); // Change to pass String ID
    }

    public DestinationAdapter(List<Map<String, Object>> destinations, List<String> destinationIds, OnDestinationClickListener listener) {
        this.destinations = destinations;
        this.destinationIds = destinationIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Map<String, Object> destination = destinations.get(position);
        String name = (String) destination.get("name");
        String imageURL = (String) destination.get("imageURL");
        String price = (String) destination.get("price");

        holder.destinationN.setText(name);
        holder.price.setText(price);


        float ratingValue = 3.5f;

        // Check if the rating value is not null
        if (destination.get("rating") != null) {
            String rating = (String) destination.get("rating");
            try {
                // Try parsing the rating value
                ratingValue = Float.parseFloat(rating);
            } catch (NumberFormatException e) {
                // Log the error and use the default value
                Log.e("DestinationAdapter", "Invalid rating value '" + rating + "'", e);
                ratingValue = 3.5f; // Default value if parsing fails
            }
        } else {
            // Log that the rating was null and a default value is used
            Log.e("DestinationAdapter", "Rating is null, defaulting to 3.5");
        }

        holder.ratingValue.setRating(ratingValue);


        if (imageURL != null) {
            Picasso.get().load(imageURL).into(holder.desimage);
        } else {
            holder.desimage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && destinationIds.size() > position) {
                listener.onDestinationClick(destinationIds.get(position)); // Pass the ID on click
            }
        });
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView destinationN, price;
        ImageView desimage;
        RatingBar ratingValue;

        ViewHolder(View itemView) {
            super(itemView);
            destinationN = itemView.findViewById(R.id.destinationName);
            price = itemView.findViewById(R.id.destinationPrice);
            desimage = itemView.findViewById(R.id.destinationImage);
            ratingValue = itemView.findViewById(R.id.ratingBar3);

        }
    }
}
