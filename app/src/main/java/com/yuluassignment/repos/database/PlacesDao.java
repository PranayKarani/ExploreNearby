package com.yuluassignment.repos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.yuluassignment.entities.Place;

import java.util.List;

@Dao
public interface PlacesDao {

    @Query("SELECT * FROM Place WHERE name LIKE :name")
    List<Place> findPlaces(String name);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Place> places);

    @Query("DELETE FROM Place")
    void deleteAll();

}
