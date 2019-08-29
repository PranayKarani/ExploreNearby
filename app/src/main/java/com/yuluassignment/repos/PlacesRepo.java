package com.yuluassignment.repos;

import android.util.Log;
import com.yuluassignment.C;
import com.yuluassignment.entities.Place;
import com.yuluassignment.entities.SimpleLocation;
import com.yuluassignment.misc.NetworkManager;
import com.yuluassignment.misc.Prefs;
import com.yuluassignment.misc.SharedPrefs;
import com.yuluassignment.repos.database.LocalDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlacesRepo implements NetworkManager.RequestListener {

    private static PlacesRepo          repo;
    private        NetworkManager      nm;
    private        LocalDatabase       db;
    private        PlacesFetchListener listener;
    private        SimpleLocation      location;

    private PlacesRepo() {

        nm = NetworkManager.get();
        db = LocalDatabase.get();

    }

    public static PlacesRepo get() {
        if (repo == null) {
            repo = new PlacesRepo();
        }
        return repo;
    }

    public void getPlacesFor(String query, SimpleLocation location, PlacesFetchListener listener) {
        this.location = location;
        this.listener = listener;
        if (!nm.connectedToInternet() || Prefs.offline) {
            db.findPlacesByName(query, listener);
        } else {
            String url = getRequestUrl(query);
            Log.i(C.TAG, "url: " + url);
            nm.makeGETRequest(url, this);
        }

    }

    public void clearLocalData() {

        db.clearDatabase();

    }

    @Override
    public void onRequestSuccess(JSONObject jsonResponse) {

        try {

            ArrayList<Place> places     = new ArrayList<>();
            JSONArray        venueArray = jsonResponse.getJSONArray("venues");

            for (int i = 0; i < venueArray.length(); i++) {
                JSONObject jo = venueArray.getJSONObject(i);
                places.add(extractFromJSON(jo));
            }

            if (listener != null) {
                listener.onPlacesFetchSuccess(places);
            }
            db.addPlaces(places);


        } catch (JSONException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onPlaceFetchFail("Something went wrong parsing response");
            }
        }

    }

    @Override
    public void onRequestFail(int err, String message) {

    }

    private Place extractFromJSON(JSONObject jo) {
        Place place = new Place();
        place.id = jo.optString("id");
        place.name = jo.optString("name");

        JSONObject locationObject = jo.optJSONObject("location");
        if (locationObject != null) {
            place.lat = locationObject.optDouble("lat");
            place.lng = locationObject.optDouble("lng");
            place.distance = locationObject.optDouble("distance");

            JSONArray addressArray = locationObject.optJSONArray("formattedAddress");
            if (addressArray != null) {
                StringBuilder address = new StringBuilder();
                int           arrLen  = addressArray.length();
                for (int i = 0; i < arrLen; i++) {
                    String value = addressArray.optString(i);
                    if (i != 0) {
                        address.append(", ");
                    } else {
                        place.shortAddress = value;
                    }
                    address.append(value);

                }

                place.fullAddress = address.toString();
            }
        }

        JSONArray categoryArray = jo.optJSONArray("categories");
        if (categoryArray != null) {
            if (categoryArray.length() > 0) {
                try {
                    place.categoryName = categoryArray.optJSONObject(0).optString("name");
                } catch (Exception ignore) {

                }
            }
        }

        return place;
    }

    private String getRequestUrl(String query) {

        StringBuilder builder = new StringBuilder();
        float         lat     = -1;
        float         lng     = -1;
        if (location != null) {
            lat = location.lat;
            lng = location.lng;
        } else {
            lat = SharedPrefs.get(C.sp_last_lat);
            lng = SharedPrefs.get(C.sp_last_long);
        }
        String locationStr;
        if (lat == -1 || lng == -1) {
            locationStr = "near=Bengaluru";
        } else {
            locationStr = "ll=" + lat + "," + lng;
        }

        builder.append(C.API_SEARCH)
                .append("client_id=").append(C.FS_CLIENT_ID).append("&")
                .append("client_secret=").append(C.FS_CLIENT_SECRET).append("&")
                .append("v=20191231&")
                .append("limit=20&")
                .append("intent=checkin&")
                .append(locationStr);

        if (query != null) {
            builder.append("&query=").append(query);
        }

        return builder.toString();

    }

    public interface PlacesFetchListener {

        void onPlacesFetchSuccess(List<Place> places);

        default void onPlaceFetchFail(String message) {
            Log.e(C.TAG, "Something went wrong");
        }

    }

}
