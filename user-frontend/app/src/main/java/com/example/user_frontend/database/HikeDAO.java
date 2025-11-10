package com.example.user_frontend.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.user_frontend.models.Hike;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HikeDAO {
    private DatabaseHelper dbHelper;
    
    public HikeDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }
    
    public long insertHike(Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(hike);
        long result = db.insert(DatabaseHelper.TABLE_HIKES, null, values);
        db.close();
        return result;
    }
    
    public List<Hike> getAllHikes() {
        List<Hike> hikes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_HIKES,
            null,
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_HIKE_DATE + " DESC"
        );
        
        if (cursor.moveToFirst()) {
            do {
                hikes.add(cursorToHike(cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return hikes;
    }
    
    public Hike getHikeById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Hike hike = null;
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_HIKES,
            null,
            DatabaseHelper.COLUMN_HIKE_ID + " = ?",
            new String[]{id},
            null,
            null,
            null
        );
        
        if (cursor.moveToFirst()) {
            hike = cursorToHike(cursor);
        }
        
        cursor.close();
        db.close();
        return hike;
    }
    
    public int updateHike(Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(hike);
        
        int rowsAffected = db.update(
            DatabaseHelper.TABLE_HIKES,
            values,
            DatabaseHelper.COLUMN_HIKE_ID + " = ?",
            new String[]{hike.getId()}
        );
        
        db.close();
        return rowsAffected;
    }
    
    public int deleteHike(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(
            DatabaseHelper.TABLE_HIKES,
            DatabaseHelper.COLUMN_HIKE_ID + " = ?",
            new String[]{id}
        );
        db.close();
        return rowsAffected;
    }
    
    public void deleteAllHikes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_HIKES, null, null);
        db.close();
    }
    
    public List<Hike> searchHikesByName(String query) {
        List<Hike> hikes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_HIKES,
            null,
            DatabaseHelper.COLUMN_HIKE_NAME + " LIKE ?",
            new String[]{"%" + query + "%"},
            null,
            null,
            DatabaseHelper.COLUMN_HIKE_DATE + " DESC"
        );
        
        if (cursor.moveToFirst()) {
            do {
                hikes.add(cursorToHike(cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return hikes;
    }
    
    public List<Hike> advancedSearch(String name, String location, Float minLength, 
                                     Float maxLength, Long startDate, Long endDate) {
        List<Hike> hikes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            selection.append(DatabaseHelper.COLUMN_HIKE_NAME).append(" LIKE ?");
            selectionArgs.add("%" + name + "%");
        }
        
        if (location != null && !location.isEmpty()) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DatabaseHelper.COLUMN_HIKE_LOCATION).append(" LIKE ?");
            selectionArgs.add("%" + location + "%");
        }
        
        if (minLength != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DatabaseHelper.COLUMN_HIKE_LENGTH).append(" >= ?");
            selectionArgs.add(String.valueOf(minLength));
        }
        
        if (maxLength != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DatabaseHelper.COLUMN_HIKE_LENGTH).append(" <= ?");
            selectionArgs.add(String.valueOf(maxLength));
        }
        
        if (startDate != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DatabaseHelper.COLUMN_HIKE_DATE).append(" >= ?");
            selectionArgs.add(String.valueOf(startDate));
        }
        
        if (endDate != null) {
            if (selection.length() > 0) selection.append(" AND ");
            selection.append(DatabaseHelper.COLUMN_HIKE_DATE).append(" <= ?");
            selectionArgs.add(String.valueOf(endDate));
        }
        
        String[] selectionArgsArray = selectionArgs.toArray(new String[0]);
        
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_HIKES,
            null,
            selection.toString(),
            selectionArgsArray,
            null,
            null,
            DatabaseHelper.COLUMN_HIKE_DATE + " DESC"
        );
        
        if (cursor.moveToFirst()) {
            do {
                hikes.add(cursorToHike(cursor));
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return hikes;
    }

    private ContentValues getContentValues(Hike hike) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HIKE_ID, hike.getId());
        values.put(DatabaseHelper.COLUMN_HIKE_NAME, hike.getName());
        values.put(DatabaseHelper.COLUMN_HIKE_LOCATION, hike.getLocation());
        values.put(DatabaseHelper.COLUMN_HIKE_DATE, hike.getDate().getTime());
        values.put(DatabaseHelper.COLUMN_HIKE_PARKING, hike.isParkingAvailable() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_HIKE_LENGTH, hike.getLength());
        values.put(DatabaseHelper.COLUMN_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(DatabaseHelper.COLUMN_HIKE_DESCRIPTION, hike.getDescription());
        values.put(DatabaseHelper.COLUMN_HIKE_DURATION, hike.getEstimatedDuration());
        values.put(DatabaseHelper.COLUMN_HIKE_WEATHER, hike.getWeatherConditions());
        values.put(DatabaseHelper.COLUMN_HIKE_CREATED_AT, hike.getCreatedAt().getTime());
        values.put(DatabaseHelper.COLUMN_HIKE_UPDATED_AT, hike.getUpdatedAt().getTime());
        return values;
    }
    
    private Hike cursorToHike(Cursor cursor) {
        Hike hike = new Hike();
        hike.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_ID)));
        hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_NAME)));
        hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_LOCATION)));
        hike.setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_DATE))));
        hike.setParkingAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_PARKING)) == 1);
        hike.setLength(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_LENGTH)));
        hike.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_DIFFICULTY)));
        hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_DESCRIPTION)));
        hike.setEstimatedDuration(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_DURATION)));
        hike.setWeatherConditions(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_WEATHER)));
        hike.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_CREATED_AT))));
        hike.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIKE_UPDATED_AT))));
        return hike;
    }
}

