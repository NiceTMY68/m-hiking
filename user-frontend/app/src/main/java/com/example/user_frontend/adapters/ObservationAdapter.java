package com.example.user_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user_frontend.R;
import com.example.user_frontend.models.Observation;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {
    
    private List<Observation> observations;
    private Context context;
    private OnObservationClickListener listener;
    private SimpleDateFormat timeFormat;
    
    public interface OnObservationClickListener {
        void onEditClick(Observation observation);
        void onDeleteClick(Observation observation);
    }
    
    public ObservationAdapter(Context context, OnObservationClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.observations = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }
    
    public void setObservations(List<Observation> observations) {
        this.observations = observations;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_observation, parent, false);
        return new ObservationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ObservationViewHolder holder, int position) {
        Observation observation = observations.get(position);
        
        holder.tvObservation.setText(observation.getObservation());
        holder.tvTime.setText(timeFormat.format(observation.getTime()));
        
        String category = observation.getCategory();
        if (category != null && !category.isEmpty()) {
            holder.tvCategory.setText(category.toUpperCase());
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }
        
        String comments = observation.getComments();
        if (comments != null && !comments.isEmpty()) {
            holder.tvComments.setText(comments);
            holder.tvComments.setVisibility(View.VISIBLE);
        } else {
            holder.tvComments.setVisibility(View.GONE);
        }
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(observation);
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(observation);
        });
    }
    
    @Override
    public int getItemCount() {
        return observations.size();
    }
    
    static class ObservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvObservation;
        TextView tvTime;
        TextView tvCategory;
        TextView tvComments;
        MaterialButton btnEdit;
        MaterialButton btnDelete;
        
        ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvObservation = itemView.findViewById(R.id.tvObservation);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvComments = itemView.findViewById(R.id.tvComments);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

