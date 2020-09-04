package com.sar.photobook;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sar.photobook.models.album;

@Database(entities = {album.class}, exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String db_name = "album_db";
    public static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, db_name)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract AlbumDao albumDao();
}
