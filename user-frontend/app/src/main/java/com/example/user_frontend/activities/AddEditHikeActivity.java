package com.example.user_frontend.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user_frontend.R;
import com.example.user_frontend.database.HikeDAO;
import com.example.user_frontend.models.Hike;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditHikeActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private TextInputEditText etHikeName;
    private TextInputEditText etLocation;
    private TextInputEditText etDate;
    private MaterialCheckBox cbParkingAvailable;
    private TextInputEditText etLength;
    private Spinner spinnerDifficulty;
    private TextInputEditText etDescription;
    private TextInputEditText etEstimatedDuration;
    private TextInputEditText etWeatherConditions;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;
    
    private HikeDAO hikeDAO;
    private Hike currentHike;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_hike);
        
        hikeDAO = new HikeDAO(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        selectedDate = Calendar.getInstance();
        
        initViews();
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        setupDifficultySpinner();
        checkEditMode();
        setupClickListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etHikeName = findViewById(R.id.etHikeName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        cbParkingAvailable = findViewById(R.id.cbParkingAvailable);
        etLength = findViewById(R.id.etLength);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        etDescription = findViewById(R.id.etDescription);
        etEstimatedDuration = findViewById(R.id.etEstimatedDuration);
        etWeatherConditions = findViewById(R.id.etWeatherConditions);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }
    
    private void setupDifficultySpinner() {
        String[] difficulties = {
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_moderate),
            getString(R.string.difficulty_hard),
            getString(R.string.difficulty_expert)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            difficulties
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);
        spinnerDifficulty.setSelection(1); // Default to Moderate
    }
    
    private void checkEditMode() {
        String hikeId = getIntent().getStringExtra("HIKE_ID");
        if (hikeId != null) {
            isEditMode = true;
            currentHike = hikeDAO.getHikeById(hikeId);
            if (currentHike != null) {
                toolbar.setTitle(R.string.title_edit_hike);
                populateFields();
            }
        } else {
            currentHike = new Hike();
        }
    }
    
    private void populateFields() {
        if (currentHike == null) return;
        
        etHikeName.setText(currentHike.getName());
        etLocation.setText(currentHike.getLocation());
        
        if (currentHike.getDate() != null) {
            selectedDate.setTime(currentHike.getDate());
            etDate.setText(dateFormat.format(currentHike.getDate()));
        }
        
        cbParkingAvailable.setChecked(currentHike.isParkingAvailable());
        etLength.setText(String.valueOf(currentHike.getLength()));
        etDescription.setText(currentHike.getDescription());
        etEstimatedDuration.setText(currentHike.getEstimatedDuration());
        etWeatherConditions.setText(currentHike.getWeatherConditions());
        
        // Set difficulty
        String difficulty = currentHike.getDifficulty();
        if (difficulty != null) {
            int position = 1; // default moderate
            switch (difficulty.toLowerCase()) {
                case "easy": position = 0; break;
                case "moderate": position = 1; break;
                case "hard": position = 2; break;
                case "expert": position = 3; break;
            }
            spinnerDifficulty.setSelection(position);
        }
    }
    
    private void setupClickListeners() {
        etDate.setOnClickListener(v -> showDatePicker());
        
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveHike();
            }
        });
        
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                etDate.setText(dateFormat.format(selectedDate.getTime()));
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private boolean validateInputs() {
        // Validate Hike Name
        String name = etHikeName.getText().toString().trim();
        if (name.isEmpty()) {
            etHikeName.setError(getString(R.string.error_required));
            etHikeName.requestFocus();
            return false;
        }
        
        // Validate Location
        String location = etLocation.getText().toString().trim();
        if (location.isEmpty()) {
            etLocation.setError(getString(R.string.error_required));
            etLocation.requestFocus();
            return false;
        }
        
        // Validate Date
        String date = etDate.getText().toString().trim();
        if (date.isEmpty()) {
            etDate.setError(getString(R.string.error_required));
            etDate.requestFocus();
            return false;
        }
        
        // Validate Length
        String lengthStr = etLength.getText().toString().trim();
        if (lengthStr.isEmpty()) {
            etLength.setError(getString(R.string.error_required));
            etLength.requestFocus();
            return false;
        }
        
        try {
            float length = Float.parseFloat(lengthStr);
            if (length <= 0) {
                etLength.setError(getString(R.string.error_invalid_length));
                etLength.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etLength.setError(getString(R.string.error_invalid_length));
            etLength.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void saveHike() {
        // Get values from inputs
        String name = etHikeName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        Date date = selectedDate.getTime();
        boolean parkingAvailable = cbParkingAvailable.isChecked();
        float length = Float.parseFloat(etLength.getText().toString().trim());
        
        String difficultyText = spinnerDifficulty.getSelectedItem().toString();
        String difficulty = "moderate"; // default
        if (difficultyText.equals(getString(R.string.difficulty_easy))) difficulty = "easy";
        else if (difficultyText.equals(getString(R.string.difficulty_moderate))) difficulty = "moderate";
        else if (difficultyText.equals(getString(R.string.difficulty_hard))) difficulty = "hard";
        else if (difficultyText.equals(getString(R.string.difficulty_expert))) difficulty = "expert";
        
        String description = etDescription.getText().toString().trim();
        String estimatedDuration = etEstimatedDuration.getText().toString().trim();
        String weatherConditions = etWeatherConditions.getText().toString().trim();
        
        // Update hike object
        currentHike.setName(name);
        currentHike.setLocation(location);
        currentHike.setDate(date);
        currentHike.setParkingAvailable(parkingAvailable);
        currentHike.setLength(length);
        currentHike.setDifficulty(difficulty);
        currentHike.setDescription(description);
        currentHike.setEstimatedDuration(estimatedDuration);
        currentHike.setWeatherConditions(weatherConditions);
        
        // Save to database
        long result;
        if (isEditMode) {
            result = hikeDAO.updateHike(currentHike);
            Toast.makeText(this, R.string.msg_hike_updated, Toast.LENGTH_SHORT).show();
        } else {
            result = hikeDAO.insertHike(currentHike);
            Toast.makeText(this, R.string.msg_hike_saved, Toast.LENGTH_SHORT).show();
        }
        
        if (result != -1) {
            finish();
        } else {
            Toast.makeText(this, "Error saving hike", Toast.LENGTH_SHORT).show();
        }
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

