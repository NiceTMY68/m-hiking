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
import com.example.user_frontend.adapters.HikeAdapter;
import com.example.user_frontend.database.HikeDAO;
import com.example.user_frontend.models.Hike;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class HikeListActivity extends AppCompatActivity implements HikeAdapter.OnHikeClickListener {
    
    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewHikes;
    private TextView tvEmptyMessage;
    
    private HikeDAO hikeDAO;
    private HikeAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_list);
        
        // Initialize DAO
        hikeDAO = new HikeDAO(this);
        
        // Initialize views
        initViews();
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Load hikes
        loadHikes();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadHikes();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewHikes = findViewById(R.id.recyclerViewHikes);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }
    
    private void setupRecyclerView() {
        adapter = new HikeAdapter(this, this);
        recyclerViewHikes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHikes.setAdapter(adapter);
    }
    
    private void loadHikes() {
        List<Hike> hikes = hikeDAO.getAllHikes();
        
        if (hikes.isEmpty()) {
            recyclerViewHikes.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerViewHikes.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
            adapter.setHikes(hikes);
        }
    }
    
    @Override
    public void onHikeClick(Hike hike) {
        Intent intent = new Intent(this, HikeDetailsActivity.class);
        intent.putExtra("HIKE_ID", hike.getId());
        startActivity(intent);
    }
    
    @Override
    public void onEditClick(Hike hike) {
        Intent intent = new Intent(this, AddEditHikeActivity.class);
        intent.putExtra("HIKE_ID", hike.getId());
        startActivity(intent);
    }
    
    @Override
    public void onDeleteClick(Hike hike) {
        new AlertDialog.Builder(this)
            .setTitle(R.string.btn_delete)
            .setMessage(R.string.msg_confirm_delete)
            .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                hikeDAO.deleteHike(hike.getId());
                Toast.makeText(this, R.string.msg_hike_deleted, Toast.LENGTH_SHORT).show();
                loadHikes();
            })
            .setNegativeButton(R.string.btn_cancel, null)
            .show();
    }
    
    @Override
    public void onObservationsClick(Hike hike) {
        Intent intent = new Intent(this, ObservationListActivity.class);
        intent.putExtra("HIKE_ID", hike.getId());
        intent.putExtra("HIKE_NAME", hike.getName());
        startActivity(intent);
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

