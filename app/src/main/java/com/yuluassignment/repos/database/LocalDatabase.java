package com.yuluassignment.repos.database;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.yuluassignment.C;
import com.yuluassignment.MyApp;
import com.yuluassignment.entities.Place;
import com.yuluassignment.repos.PlacesRepo;

import java.util.List;

@Database(entities = {Place.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase db;

    public static LocalDatabase get() {

        if (db == null) {
            db = Room.databaseBuilder(MyApp.get(), LocalDatabase.class, "database").build();
        }
        return db;

    }

    public abstract PlacesDao placesDao();

    @SuppressLint("StaticFieldLeak")
    public void addPlaces(List<Place> places) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(C.TAG, "inserting into database...");
                db.placesDao().insertAll(places);
                Log.d(C.TAG, "done inserting");
                return null;
            }
        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    public void findPlacesByName(String name, PlacesRepo.PlacesFetchListener listener) {

        new AsyncTask<Void, Void, List<Place>>() {
            @Override
            protected List<Place> doInBackground(Void... voids) {
                Log.d(C.TAG, "getting places from database...");
                return db.placesDao().findPlaces("%" + name + "%");
            }

            @Override
            protected void onPostExecute(List<Place> places) {
                super.onPostExecute(places);
                Log.d(C.TAG, "got " + places.size() + " places");
                listener.onPlacesFetchSuccess(places);
            }
        }.execute();

    }

    @SuppressLint("StaticFieldLeak")
    public void clearDatabase() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(C.TAG, "deleting database...");
                db.placesDao().deleteAll();
                return null;
            }
        }.execute();

    }


}
