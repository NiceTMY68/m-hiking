package com.example.user_frontend;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user_frontend.activities.AddEditHikeActivity;
import com.example.user_frontend.activities.HikeListActivity;
import com.example.user_frontend.activities.SearchActivity;
import com.example.user_frontend.database.DatabaseHelper;
import com.example.user_frontend.database.HikeDAO;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private MaterialButton btnAddHike;
    private MaterialButton btnViewHikes;
    private MaterialButton btnSearch;
    private MaterialButton btnDeleteAll;
    private TextView tvHikeCount;
    
    private HikeDAO hikeDAO;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        hikeDAO = new HikeDAO(this);
        
        initViews();
        setSupportActionBar(toolbar);
        setupClickListeners();
        updateHikeCount();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateHikeCount();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btnAddHike = findViewById(R.id.btnAddHike);
        btnViewHikes = findViewById(R.id.btnViewHikes);
        btnSearch = findViewById(R.id.btnSearch);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        tvHikeCount = findViewById(R.id.tvHikeCount);
    }
    
    private void setupClickListeners() {
        btnAddHike.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditHikeActivity.class);
            startActivity(intent);
        });
        
        btnViewHikes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HikeListActivity.class);
            startActivity(intent);
        });
        
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        
        
        btnDeleteAll.setOnClickListener(v -> confirmDeleteAll());
    }
    
    private void updateHikeCount() {
        int count = hikeDAO.getAllHikes().size();
        tvHikeCount.setText(getString(R.string.hikes_count, count));
    }
    
    
    private void confirmDeleteAll() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.btn_delete_all)
            .setMessage(R.string.msg_confirm_delete_all)
            .setPositiveButton(R.string.btn_delete, (dialog, which) -> deleteAllData())
            .setNegativeButton(R.string.btn_cancel, null)
            .show();
    }
    
    private void deleteAllData() {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        dbHelper.deleteAllData();
        updateHikeCount();
        Toast.makeText(this, "All data deleted", Toast.LENGTH_SHORT).show();
    }
}
