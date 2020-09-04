package com.sar.photobook;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.sar.photobook.models.album;

import java.util.List;

@Dao
public interface AlbumDao {

    @Query("SELECT * FROM albums")
    List<album> getAll();

    @Query("SELECT * FROM albums WHERE uid IN (:userIds)")
    List<album> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(album ALBUM);

    @Delete
    void delete(album ALBUM);
}
