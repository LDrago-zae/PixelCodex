package com.example.pixelcodex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_WISHLIST = "wishlist";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE_RES_ID = "image_res_id";

    // Create table query
    private static final String CREATE_TABLE_WISHLIST = "CREATE TABLE " + TABLE_WISHLIST + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_IMAGE_RES_ID + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WISHLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        onCreate(db);
    }

    // Add a game to the wishlist
    public long addGameToWishlist(String title, int imageResId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_IMAGE_RES_ID, imageResId);
        long id = db.insert(TABLE_WISHLIST, null, values);
        db.close();
        return id;
    }

    // Remove a game from the wishlist by title
    public void removeGameFromWishlist(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WISHLIST, COLUMN_TITLE + "=?", new String[]{title});
        db.close();
    }

    // Check if a game is in the wishlist
    public boolean isGameInWishlist(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WISHLIST, new String[]{COLUMN_ID},
                COLUMN_TITLE + "=?", new String[]{title}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Get all games in the wishlist
    public List<Game> getWishlistGames() {
        List<Game> gameList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WISHLIST, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID));
                gameList.add(new Game(title, imageResId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return gameList;
    }
}