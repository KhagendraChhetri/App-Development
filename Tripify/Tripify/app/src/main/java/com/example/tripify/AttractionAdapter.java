package com.example.tripify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Map;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {
    private final List<Map<String, Object>> attractions;

    public AttractionAdapter(List<Map<String, Object>> attractions) {
        this.attractions = attractions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> attraction = attractions.get(position);
        holder.txtAttractionName.setText((String) attraction.get("name"));
        holder.txtAttractionDescription.setText((String) attraction.get("description"));
        Picasso.get().load((String) attraction.get("imageURL")).into(holder.imgAttraction);
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAttractionName, txtAttractionDescription;
        ImageView imgAttraction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAttractionName = itemView.findViewById(R.id.txt_attraction_name);
            txtAttractionDescription = itemView.findViewById(R.id.txt_attraction_description);
            imgAttraction = itemView.findViewById(R.id.img_attraction);
        }
    }
}
