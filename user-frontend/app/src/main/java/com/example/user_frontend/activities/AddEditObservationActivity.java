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
        
        hikeId = getIntent().getStringExtra("HIKE_ID");
        
        if (hikeId == null) {
            Toast.makeText(this, "Error: No hike selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        observationDAO = new ObservationDAO(this);
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        selectedTime = Calendar.getInstance();
        
        initViews();
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        setupCategorySpinner();
        checkEditMode();
        setupClickListeners();
        
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
        
        String category = currentObservation.getCategory();
        if (category != null) {
            spinnerCategory.setSelection(getCategoryPosition(category));
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
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
        currentObservation.setObservation(etObservation.getText().toString().trim());
        currentObservation.setTime(selectedTime.getTime());
        currentObservation.setComments(etComments.getText().toString().trim());
        currentObservation.setCategory(getCategoryFromString(spinnerCategory.getSelectedItem().toString()));
        
        long result = isEditMode ? observationDAO.updateObservation(currentObservation) : observationDAO.insertObservation(currentObservation);
        
        if (result != -1) {
            Toast.makeText(this, R.string.msg_observation_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving observation", Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getCategoryFromString(String categoryText) {
        if (categoryText.equals(getString(R.string.category_wildlife))) return "wildlife";
        if (categoryText.equals(getString(R.string.category_weather))) return "weather";
        if (categoryText.equals(getString(R.string.category_trail_conditions))) return "trail_conditions";
        if (categoryText.equals(getString(R.string.category_scenery))) return "scenery";
        return "other";
    }

    private int getCategoryPosition(String category) {
        switch (category.toLowerCase()) {
            case "wildlife": return 0;
            case "weather": return 1;
            case "trail_conditions": return 2;
            case "scenery": return 3;
            default: return 4;
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

