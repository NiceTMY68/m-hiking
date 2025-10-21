package com.example.user_frontend.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements HikeAdapter.OnHikeClickListener {
    
    private MaterialToolbar toolbar;
    private SwitchMaterial switchAdvanced;
    private LinearLayout layoutBasicSearch;
    private LinearLayout layoutAdvancedSearch;
    
    // Basic search
    private TextInputEditText etBasicSearch;
    private MaterialButton btnBasicSearch;
    
    // Advanced search
    private TextInputEditText etAdvancedName;
    private TextInputEditText etAdvancedLocation;
    private TextInputEditText etMinLength;
    private TextInputEditText etMaxLength;
    private TextInputEditText etStartDate;
    private TextInputEditText etEndDate;
    private MaterialButton btnAdvancedSearch;
    private MaterialButton btnClear;
    
    // Results
    private TextView tvResultsHeader;
    private RecyclerView recyclerViewResults;
    private TextView tvNoResults;
    
    private HikeDAO hikeDAO;
    private HikeAdapter adapter;
    private SimpleDateFormat dateFormat;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        hikeDAO = new HikeDAO(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        
        initViews();
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        setupRecyclerView();
        setupClickListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        switchAdvanced = findViewById(R.id.switchAdvanced);
        layoutBasicSearch = findViewById(R.id.layoutBasicSearch);
        layoutAdvancedSearch = findViewById(R.id.layoutAdvancedSearch);
        
        etBasicSearch = findViewById(R.id.etBasicSearch);
        btnBasicSearch = findViewById(R.id.btnBasicSearch);
        
        etAdvancedName = findViewById(R.id.etAdvancedName);
        etAdvancedLocation = findViewById(R.id.etAdvancedLocation);
        etMinLength = findViewById(R.id.etMinLength);
        etMaxLength = findViewById(R.id.etMaxLength);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnAdvancedSearch = findViewById(R.id.btnAdvancedSearch);
        btnClear = findViewById(R.id.btnClear);
        
        tvResultsHeader = findViewById(R.id.tvResultsHeader);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);
        tvNoResults = findViewById(R.id.tvNoResults);
    }
    
    private void setupRecyclerView() {
        adapter = new HikeAdapter(this, this);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        switchAdvanced.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutBasicSearch.setVisibility(View.GONE);
                layoutAdvancedSearch.setVisibility(View.VISIBLE);
            } else {
                layoutBasicSearch.setVisibility(View.VISIBLE);
                layoutAdvancedSearch.setVisibility(View.GONE);
            }
            hideResults();
        });
        
        btnBasicSearch.setOnClickListener(v -> performBasicSearch());
        
        btnAdvancedSearch.setOnClickListener(v -> performAdvancedSearch());
        
        btnClear.setOnClickListener(v -> clearAdvancedFields());
        
        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));
    }
    
    private void performBasicSearch() {
        String query = etBasicSearch.getText().toString().trim();
        
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<Hike> results = hikeDAO.searchHikesByName(query);
        displayResults(results);
    }
    
    private void performAdvancedSearch() {
        String name = etAdvancedName.getText().toString().trim();
        String location = etAdvancedLocation.getText().toString().trim();
        
        Float minLength = null;
        String minLengthStr = etMinLength.getText().toString().trim();
        if (!minLengthStr.isEmpty()) {
            try {
                minLength = Float.parseFloat(minLengthStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid min length", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        Float maxLength = null;
        String maxLengthStr = etMaxLength.getText().toString().trim();
        if (!maxLengthStr.isEmpty()) {
            try {
                maxLength = Float.parseFloat(maxLengthStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid max length", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        Long startDate = null;
        if (!etStartDate.getText().toString().isEmpty()) {
            startDate = startDateCalendar.getTimeInMillis();
        }
        
        Long endDate = null;
        if (!etEndDate.getText().toString().isEmpty()) {
            endDate = endDateCalendar.getTimeInMillis();
        }
        
        // Check if at least one criteria is provided
        if (name.isEmpty() && location.isEmpty() && minLength == null && 
            maxLength == null && startDate == null && endDate == null) {
            Toast.makeText(this, "Please enter at least one search criteria", 
                Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<Hike> results = hikeDAO.advancedSearch(
            name.isEmpty() ? null : name,
            location.isEmpty() ? null : location,
            minLength,
            maxLength,
            startDate,
            endDate
        );
        
        displayResults(results);
    }
    
    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                String dateStr = dateFormat.format(calendar.getTime());
                if (isStartDate) {
                    etStartDate.setText(dateStr);
                } else {
                    etEndDate.setText(dateStr);
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void clearAdvancedFields() {
        etAdvancedName.setText("");
        etAdvancedLocation.setText("");
        etMinLength.setText("");
        etMaxLength.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        hideResults();
    }
    
    private void displayResults(List<Hike> results) {
        if (results.isEmpty()) {
            recyclerViewResults.setVisibility(View.GONE);
            tvResultsHeader.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            recyclerViewResults.setVisibility(View.VISIBLE);
            tvResultsHeader.setVisibility(View.VISIBLE);
            tvResultsHeader.setText("Search Results (" + results.size() + ")");
            tvNoResults.setVisibility(View.GONE);
            adapter.setHikes(results);
        }
    }
    
    private void hideResults() {
        recyclerViewResults.setVisibility(View.GONE);
        tvResultsHeader.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
    }
    
    // HikeAdapter.OnHikeClickListener implementation
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
        hikeDAO.deleteHike(hike.getId());
        Toast.makeText(this, R.string.msg_hike_deleted, Toast.LENGTH_SHORT).show();
        // Re-perform last search
        if (switchAdvanced.isChecked()) {
            performAdvancedSearch();
        } else {
            performBasicSearch();
        }
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

