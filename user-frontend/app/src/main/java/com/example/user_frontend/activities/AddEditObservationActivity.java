package com.example.user_frontend.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.user_frontend.R;
import com.example.user_frontend.database.ObservationDAO;
import com.example.user_frontend.models.Observation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditObservationActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private TextInputEditText etObservation;
    private TextInputEditText etTime;
    private Spinner spinnerCategory;
    private TextInputEditText etComments;
    private MaterialButton btnSave;
    private MaterialButton btnCancel;
    
    private ObservationDAO observationDAO;
    private Observation currentObservation;
    private String hikeId;
    private Calendar selectedTime;
    private SimpleDateFormat dateTimeFormat;
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_observation);
        
        // Get hike ID from intent
        hikeId = getIntent().getStringExtra("HIKE_ID");
        
        if (hikeId == null) {
            Toast.makeText(this, "Error: No hike selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Initialize DAO
        observationDAO = new ObservationDAO(this);
        
        // Initialize date/time formatter
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        selectedTime = Calendar.getInstance();
        
        // Initialize views
        initViews();
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Set up spinner
        setupCategorySpinner();
        
        // Check if editing existing observation
        checkEditMode();
        
        // Set up click listeners
        setupClickListeners();
        
        // Set default time
        if (!isEditMode) {
            etTime.setText(dateTimeFormat.format(selectedTime.getTime()));
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etObservation = findViewById(R.id.etObservation);
        etTime = findViewById(R.id.etTime);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etComments = findViewById(R.id.etComments);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }
    
    private void setupCategorySpinner() {
        String[] categories = {
            getString(R.string.category_wildlife),
            getString(R.string.category_weather),
            getString(R.string.category_trail_conditions),
            getString(R.string.category_scenery),
            getString(R.string.category_other)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }
    
    private void checkEditMode() {
        String observationId = getIntent().getStringExtra("OBSERVATION_ID");
        if (observationId != null) {
            isEditMode = true;
            currentObservation = observationDAO.getObservationById(observationId);
            if (currentObservation != null) {
                toolbar.setTitle("Edit Observation");
                populateFields();
            }
        } else {
            currentObservation = new Observation();
            currentObservation.setHikeId(hikeId);
        }
    }
    
    private void populateFields() {
        if (currentObservation == null) return;
        
        etObservation.setText(currentObservation.getObservation());
        
        if (currentObservation.getTime() != null) {
            selectedTime.setTime(currentObservation.getTime());
            etTime.setText(dateTimeFormat.format(currentObservation.getTime()));
        }
        
        etComments.setText(currentObservation.getComments());
        
        // Set category
        String category = currentObservation.getCategory();
        if (category != null) {
            int position = 4; // default other
            switch (category.toLowerCase()) {
                case "wildlife": position = 0; break;
                case "weather": position = 1; break;
                case "trail_conditions": position = 2; break;
                case "scenery": position = 3; break;
                case "other": position = 4; break;
            }
            spinnerCategory.setSelection(position);
        }
    }
    
    private void setupClickListeners() {
        etTime.setOnClickListener(v -> showDateTimePicker());
        
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                saveObservation();
            }
        });
        
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void showDateTimePicker() {
        // First show date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                // Then show time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (timeView, hourOfDay, minute) -> {
                        selectedTime.set(year, month, dayOfMonth, hourOfDay, minute);
                        etTime.setText(dateTimeFormat.format(selectedTime.getTime()));
                    },
                    selectedTime.get(Calendar.HOUR_OF_DAY),
                    selectedTime.get(Calendar.MINUTE),
                    true
                );
                timePickerDialog.show();
            },
            selectedTime.get(Calendar.YEAR),
            selectedTime.get(Calendar.MONTH),
            selectedTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private boolean validateInputs() {
        String observation = etObservation.getText().toString().trim();
        if (observation.isEmpty()) {
            etObservation.setError(getString(R.string.error_required));
            etObservation.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void saveObservation() {
        String observation = etObservation.getText().toString().trim();
        Date time = selectedTime.getTime();
        String comments = etComments.getText().toString().trim();
        
        String categoryText = spinnerCategory.getSelectedItem().toString();
        String category = "other"; // default
        if (categoryText.equals(getString(R.string.category_wildlife))) category = "wildlife";
        else if (categoryText.equals(getString(R.string.category_weather))) category = "weather";
        else if (categoryText.equals(getString(R.string.category_trail_conditions))) category = "trail_conditions";
        else if (categoryText.equals(getString(R.string.category_scenery))) category = "scenery";
        else if (categoryText.equals(getString(R.string.category_other))) category = "other";
        
        currentObservation.setObservation(observation);
        currentObservation.setTime(time);
        currentObservation.setComments(comments);
        currentObservation.setCategory(category);
        
        long result;
        if (isEditMode) {
            result = observationDAO.updateObservation(currentObservation);
        } else {
            result = observationDAO.insertObservation(currentObservation);
        }
        
        if (result != -1) {
            Toast.makeText(this, R.string.msg_observation_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving observation", Toast.LENGTH_SHORT).show();
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

