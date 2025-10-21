package com.example.user_frontend.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user_frontend.R;
import com.example.user_frontend.models.Hike;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {
    
    private List<Hike> hikes;
    private Context context;
    private OnHikeClickListener listener;
    private SimpleDateFormat dateFormat;
    
    public interface OnHikeClickListener {
        void onHikeClick(Hike hike);
        void onEditClick(Hike hike);
        void onDeleteClick(Hike hike);
        void onObservationsClick(Hike hike);
    }
    
    public HikeAdapter(Context context, OnHikeClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.hikes = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }
    
    public void setHikes(List<Hike> hikes) {
        this.hikes = hikes;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hike, parent, false);
        return new HikeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HikeViewHolder holder, int position) {
        Hike hike = hikes.get(position);
        
        holder.tvHikeName.setText(hike.getName());
        holder.tvHikeLocation.setText(hike.getLocation());
        holder.tvHikeDate.setText(dateFormat.format(hike.getDate()));
        holder.tvHikeLength.setText(String.format(Locale.getDefault(), "%.1f km", hike.getLength()));
        
        // Set difficulty
        String difficulty = hike.getDifficulty();
        holder.tvHikeDifficulty.setText(difficulty != null ? difficulty.toUpperCase() : "MODERATE");
        
        int difficultyColor = getDifficultyColor(difficulty);
        holder.tvHikeDifficulty.setBackgroundColor(difficultyColor);
        
        // Set parking
        holder.tvHikeParking.setText(hike.isParkingAvailable() ? 
            context.getString(R.string.label_yes) : context.getString(R.string.label_no));
        
        // Remove sync status (no longer needed)
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onHikeClick(hike);
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(hike);
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(hike);
        });
        
        holder.btnAddObservation.setOnClickListener(v -> {
            if (listener != null) listener.onObservationsClick(hike);
        });
    }
    
    @Override
    public int getItemCount() {
        return hikes.size();
    }
    
    private int getDifficultyColor(String difficulty) {
        if (difficulty == null) difficulty = "moderate";
        
        switch (difficulty.toLowerCase()) {
            case "easy":
                return context.getResources().getColor(R.color.difficulty_easy, null);
            case "moderate":
                return context.getResources().getColor(R.color.difficulty_moderate, null);
            case "hard":
                return context.getResources().getColor(R.color.difficulty_hard, null);
            case "expert":
                return context.getResources().getColor(R.color.difficulty_expert, null);
            default:
                return context.getResources().getColor(R.color.difficulty_moderate, null);
        }
    }
    
    static class HikeViewHolder extends RecyclerView.ViewHolder {
        TextView tvHikeName;
        TextView tvHikeLocation;
        TextView tvHikeDate;
        TextView tvHikeLength;
        TextView tvHikeDifficulty;
        TextView tvHikeParking;
        MaterialButton btnEdit;
        MaterialButton btnDelete;
        MaterialButton btnAddObservation;
        
        HikeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHikeName = itemView.findViewById(R.id.tvHikeName);
            tvHikeLocation = itemView.findViewById(R.id.tvHikeLocation);
            tvHikeDate = itemView.findViewById(R.id.tvHikeDate);
            tvHikeLength = itemView.findViewById(R.id.tvHikeLength);
            tvHikeDifficulty = itemView.findViewById(R.id.tvHikeDifficulty);
            tvHikeParking = itemView.findViewById(R.id.tvHikeParking);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddObservation = itemView.findViewById(R.id.btnAddObservation);
        }
    }
}

