package com.example.user_frontend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "MHiking.db";
    private static final int DATABASE_VERSION = 2;
    
    public static final String TABLE_HIKES = "hikes";
    public static final String COLUMN_HIKE_ID = "id";
    public static final String COLUMN_HIKE_NAME = "name";
    public static final String COLUMN_HIKE_LOCATION = "location";
    public static final String COLUMN_HIKE_DATE = "date";
    public static final String COLUMN_HIKE_PARKING = "parking_available";
    public static final String COLUMN_HIKE_LENGTH = "length";
    public static final String COLUMN_HIKE_DIFFICULTY = "difficulty";
    public static final String COLUMN_HIKE_DESCRIPTION = "description";
    public static final String COLUMN_HIKE_DURATION = "estimated_duration";
    public static final String COLUMN_HIKE_WEATHER = "weather_conditions";
    public static final String COLUMN_HIKE_CREATED_AT = "created_at";
    public static final String COLUMN_HIKE_UPDATED_AT = "updated_at";
    
    public static final String TABLE_OBSERVATIONS = "observations";
    public static final String COLUMN_OBS_ID = "id";
    public static final String COLUMN_OBS_HIKE_ID = "hike_id";
    public static final String COLUMN_OBS_OBSERVATION = "observation";
    public static final String COLUMN_OBS_TIME = "time";
    public static final String COLUMN_OBS_COMMENTS = "comments";
    public static final String COLUMN_OBS_CATEGORY = "category";
    public static final String COLUMN_OBS_IMAGE_URL = "image_url";
    public static final String COLUMN_OBS_CREATED_AT = "created_at";
    public static final String COLUMN_OBS_UPDATED_AT = "updated_at";
    
    private static final String CREATE_TABLE_HIKES = "CREATE TABLE " + TABLE_HIKES + " ("
            + COLUMN_HIKE_ID + " TEXT PRIMARY KEY, "
            + COLUMN_HIKE_NAME + " TEXT NOT NULL, "
            + COLUMN_HIKE_LOCATION + " TEXT NOT NULL, "
            + COLUMN_HIKE_DATE + " INTEGER NOT NULL, "
            + COLUMN_HIKE_PARKING + " INTEGER DEFAULT 0, "
            + COLUMN_HIKE_LENGTH + " REAL, "
            + COLUMN_HIKE_DIFFICULTY + " TEXT NOT NULL, "
            + COLUMN_HIKE_DESCRIPTION + " TEXT, "
            + COLUMN_HIKE_DURATION + " TEXT, "
            + COLUMN_HIKE_WEATHER + " TEXT, "
            + COLUMN_HIKE_CREATED_AT + " INTEGER, "
            + COLUMN_HIKE_UPDATED_AT + " INTEGER"
            + ")";
    
    private static final String CREATE_TABLE_OBSERVATIONS = "CREATE TABLE " + TABLE_OBSERVATIONS + " ("
            + COLUMN_OBS_ID + " TEXT PRIMARY KEY, "
            + COLUMN_OBS_HIKE_ID + " TEXT NOT NULL, "
            + COLUMN_OBS_OBSERVATION + " TEXT NOT NULL, "
            + COLUMN_OBS_TIME + " INTEGER NOT NULL, "
            + COLUMN_OBS_COMMENTS + " TEXT, "
            + COLUMN_OBS_CATEGORY + " TEXT, "
            + COLUMN_OBS_IMAGE_URL + " TEXT, "
            + COLUMN_OBS_CREATED_AT + " INTEGER, "
            + COLUMN_OBS_UPDATED_AT + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_OBS_HIKE_ID + ") REFERENCES " 
            + TABLE_HIKES + "(" + COLUMN_HIKE_ID + ") ON DELETE CASCADE"
            + ")";
    
    private static DatabaseHelper instance;
    
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HIKES);
        db.execSQL(CREATE_TABLE_OBSERVATIONS);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }
    
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OBSERVATIONS, null, null);
        db.delete(TABLE_HIKES, null, null);
        db.close();
    }
}

