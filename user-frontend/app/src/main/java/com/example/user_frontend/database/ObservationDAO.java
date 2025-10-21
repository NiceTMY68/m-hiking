package com.example.user_frontend.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user_frontend.models.Observation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObservationDAO {
    private DatabaseHelper dbHelper;
    
    public ObservationDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }
    
    public long insertObservation(Observation observation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(observation);
        long result = db.insert(DatabaseHelper.TABLE_OBSERVATIONS, null, values);
        db.close();
        return result;
    }
    
    public List<Observation> getObservationsByHikeId(String hikeId) {
        List<Observation> observations = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_OBSERVATIONS,
            null,
            DatabaseHelper.COLUMN_OBS_HIKE_ID + " = ?",
            new String[]{hikeId},
            null,
            null,
            DatabaseHelper.COLUMN_OBS_TIME + " DESC"
        );
        
        if (cursor.moveToFirst()) {
            do {
                observations.add(cursorToObservation(cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return observations;
    }
    
    public List<Observation> getAllObservations() {
        List<Observation> observations = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_OBSERVATIONS,
            null,
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_OBS_TIME + " DESC"
        );
        
        if (cursor.moveToFirst()) {
            do {
                observations.add(cursorToObservation(cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return observations;
    }
    
    public Observation getObservationById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Observation observation = null;
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_OBSERVATIONS,
            null,
            DatabaseHelper.COLUMN_OBS_ID + " = ?",
            new String[]{id},
            null,
            null,
            null
        );
        
        if (cursor.moveToFirst()) {
            observation = cursorToObservation(cursor);
        }
        
        cursor.close();
        db.close();
        return observation;
    }
    
    public int updateObservation(Observation observation) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(observation);
        
        int rowsAffected = db.update(
            DatabaseHelper.TABLE_OBSERVATIONS,
            values,
            DatabaseHelper.COLUMN_OBS_ID + " = ?",
            new String[]{observation.getId()}
        );
        
        db.close();
        return rowsAffected;
    }
    
    public int deleteObservation(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
            DatabaseHelper.TABLE_OBSERVATIONS,
            DatabaseHelper.COLUMN_OBS_ID + " = ?",
            new String[]{id}
        );
        db.close();
        return rowsAffected;
    }
    
    public int deleteObservationsByHikeId(String hikeId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
            DatabaseHelper.TABLE_OBSERVATIONS,
            DatabaseHelper.COLUMN_OBS_HIKE_ID + " = ?",
            new String[]{hikeId}
        );
        db.close();
        return rowsAffected;
    }
    
    private ContentValues getContentValues(Observation observation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_OBS_ID, observation.getId());
        values.put(DatabaseHelper.COLUMN_OBS_HIKE_ID, observation.getHikeId());
        values.put(DatabaseHelper.COLUMN_OBS_OBSERVATION, observation.getObservation());
        values.put(DatabaseHelper.COLUMN_OBS_TIME, observation.getTime().getTime());
        values.put(DatabaseHelper.COLUMN_OBS_COMMENTS, observation.getComments());
        values.put(DatabaseHelper.COLUMN_OBS_CATEGORY, observation.getCategory());
        values.put(DatabaseHelper.COLUMN_OBS_IMAGE_URL, observation.getImageUrl());
        values.put(DatabaseHelper.COLUMN_OBS_CREATED_AT, observation.getCreatedAt().getTime());
        values.put(DatabaseHelper.COLUMN_OBS_UPDATED_AT, observation.getUpdatedAt().getTime());
        return values;
    }
    
    private Observation cursorToObservation(Cursor cursor) {
        Observation observation = new Observation();
        observation.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_ID)));
        observation.setHikeId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_HIKE_ID)));
        observation.setObservation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_OBSERVATION)));
        observation.setTime(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_TIME))));
        observation.setComments(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_COMMENTS)));
        observation.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_CATEGORY)));
        observation.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_IMAGE_URL)));
        observation.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_CREATED_AT))));
        observation.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_OBS_UPDATED_AT))));
        return observation;
    }
}

