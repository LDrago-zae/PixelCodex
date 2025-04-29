package com.example.pixelcodex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SessionDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PixelCodexSession.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "discord_session";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ACCESS_TOKEN = "access_token";
    private static final String COLUMN_USER_ID = "user_id";

    public SessionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ACCESS_TOKEN + " TEXT, " +
                COLUMN_USER_ID + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Save the Discord session
    public void saveSession(String accessToken, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Clear existing session (we'll only store one session at a time)
        db.execSQL("DELETE FROM " + TABLE_NAME);

        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCESS_TOKEN, accessToken);
        values.put(COLUMN_USER_ID, userId);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Retrieve the Discord session
    public String[] getSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] sessionData = new String[2]; // [accessToken, userId]
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ACCESS_TOKEN, COLUMN_USER_ID},
                null, null, null, null, null);

        if (cursor.moveToFirst()) {
            sessionData[0] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCESS_TOKEN));
            sessionData[1] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            cursor.close();
        } else {
            sessionData[0] = null;
            sessionData[1] = null;
        }
        db.close();
        return sessionData;
    }

    // Clear the session (e.g., for logout)
    public void clearSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}