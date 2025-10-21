package com.example.user_frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user_frontend.R;
import com.example.user_frontend.database.HikeDAO;
import com.example.user_frontend.database.ObservationDAO;
import com.example.user_frontend.models.Hike;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HikeDetailsActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private TextView tvName, tvLocation, tvDate, tvLength, tvDifficulty;
    private TextView tvParking, tvDescription, tvDuration, tvWeather;
    private TextView tvObservationCount;
    private MaterialButton btnEdit, btnViewObservations;
    
    private HikeDAO hikeDAO;
    private ObservationDAO observationDAO;
    private Hike hike;
    private SimpleDateFormat dateFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_details);
        
        hikeDAO = new HikeDAO(this);
        observationDAO = new ObservationDAO(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        
        initViews();
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        loadHikeDetails();
        setupClickListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvName = findViewById(R.id.tvName);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvLength = findViewById(R.id.tvLength);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        tvParking = findViewById(R.id.tvParking);
        tvDescription = findViewById(R.id.tvDescription);
        tvDuration = findViewById(R.id.tvDuration);
        tvWeather = findViewById(R.id.tvWeather);
        tvObservationCount = findViewById(R.id.tvObservationCount);
        btnEdit = findViewById(R.id.btnEdit);
        btnViewObservations = findViewById(R.id.btnViewObservations);
    }
    
    private void loadHikeDetails() {
        String hikeId = getIntent().getStringExtra("HIKE_ID");
        if (hikeId != null) {
            hike = hikeDAO.getHikeById(hikeId);
            if (hike != null) {
                displayHikeDetails();
            }
        }
    }
    
    private void displayHikeDetails() {
        tvName.setText(hike.getName());
        tvLocation.setText(hike.getLocation());
        tvDate.setText(dateFormat.format(hike.getDate()));
        tvLength.setText(String.format(Locale.getDefault(), "%.1f km", hike.getLength()));
        tvDifficulty.setText(hike.getDifficulty() != null ? 
            hike.getDifficulty().toUpperCase() : "MODERATE");
        tvParking.setText(hike.isParkingAvailable() ? 
            getString(R.string.label_yes) : getString(R.string.label_no));
        
        String desc = hike.getDescription();
        tvDescription.setText(desc != null && !desc.isEmpty() ? desc : "N/A");
        
        String duration = hike.getEstimatedDuration();
        tvDuration.setText(duration != null && !duration.isEmpty() ? duration : "N/A");
        
        String weather = hike.getWeatherConditions();
        tvWeather.setText(weather != null && !weather.isEmpty() ? weather : "N/A");
        
        int obsCount = observationDAO.getObservationsByHikeId(hike.getId()).size();
        tvObservationCount.setText(getString(R.string.observations_count, obsCount));
    }
    
    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditHikeActivity.class);
            intent.putExtra("HIKE_ID", hike.getId());
            startActivity(intent);
        });
        
        btnViewObservations.setOnClickListener(v -> {
            Intent intent = new Intent(this, ObservationListActivity.class);
            intent.putExtra("HIKE_ID", hike.getId());
            intent.putExtra("HIKE_NAME", hike.getName());
            startActivity(intent);
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadHikeDetails();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

