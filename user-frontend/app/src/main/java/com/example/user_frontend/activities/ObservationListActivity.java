package com.example.user_frontend.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user_frontend.R;
import com.example.user_frontend.adapters.ObservationAdapter;
import com.example.user_frontend.database.ObservationDAO;
import com.example.user_frontend.models.Observation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ObservationListActivity extends AppCompatActivity implements ObservationAdapter.OnObservationClickListener {
    
    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewObservations;
    private TextView tvEmptyMessage;
    private FloatingActionButton fabAddObservation;
    
    private ObservationDAO observationDAO;
    private ObservationAdapter adapter;
    private String hikeId;
    private String hikeName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_list);
        
        // Get hike info from intent
        hikeId = getIntent().getStringExtra("HIKE_ID");
        hikeName = getIntent().getStringExtra("HIKE_NAME");
        
        if (hikeId == null) {
            Toast.makeText(this, "Error: No hike selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize DAO
        observationDAO = new ObservationDAO(this);
        
        // Initialize views
        initViews();
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (hikeName != null) {
                getSupportActionBar().setSubtitle(hikeName);
            }
        }
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Set up FAB
        fabAddObservation.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditObservationActivity.class);
            intent.putExtra("HIKE_ID", hikeId);
            startActivity(intent);
        });
        
        // Load observations
        loadObservations();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadObservations();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewObservations = findViewById(R.id.recyclerViewObservations);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        fabAddObservation = findViewById(R.id.fabAddObservation);
    }
    
    private void setupRecyclerView() {
        adapter = new ObservationAdapter(this, this);
        recyclerViewObservations.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewObservations.setAdapter(adapter);
    }
    
    private void loadObservations() {
        List<Observation> observations = observationDAO.getObservationsByHikeId(hikeId);
        
        if (observations.isEmpty()) {
            recyclerViewObservations.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerViewObservations.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
            adapter.setObservations(observations);
        }
    }
    
    @Override
    public void onEditClick(Observation observation) {
        Intent intent = new Intent(this, AddEditObservationActivity.class);
        intent.putExtra("HIKE_ID", hikeId);
        intent.putExtra("OBSERVATION_ID", observation.getId());
        startActivity(intent);
    }
    
    @Override
    public void onDeleteClick(Observation observation) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.btn_delete)
            .setMessage(R.string.msg_confirm_delete)
            .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                observationDAO.deleteObservation(observation.getId());
                Toast.makeText(this, R.string.msg_observation_deleted, Toast.LENGTH_SHORT).show();
                loadObservations();
            })
            .setNegativeButton(R.string.btn_cancel, null)
            .show();
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

