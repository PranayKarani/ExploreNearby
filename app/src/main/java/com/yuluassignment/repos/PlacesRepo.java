package com.yuluassignment.repos;

import android.util.Log;
import com.yuluassignment.C;
import com.yuluassignment.entities.Place;
import com.yuluassignment.misc.NetworkManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlacesRepo {

    private static PlacesRepo     repo;
    private        NetworkManager nm;

    private PlacesRepo() {

        nm = NetworkManager.get();

    }

    public static PlacesRepo get() {
        if (repo == null) {
            repo = new PlacesRepo();
        }
        return repo;
    }

    public void getPlacesFor(String query, PlacesFetchListener listener) {

        nm.makeGETRequest(getRequestUrl(query), new NetworkManager.RequestListener() {
            @Override
            public void onSuccess(JSONObject jsonResponse) {

                try {

                    ArrayList<Place> places     = new ArrayList<>();
                    JSONArray        venueArray = jsonResponse.getJSONArray("venues");

                    for (int i = 0; i < venueArray.length(); i++) {
                        JSONObject jo = venueArray.getJSONObject(i);
                        places.add(extractFromJSON(jo));
                    }

                    listener.onPlacesFetchSuccess(places);


                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onPlaceFetchFail("Something went wrong parsing response");
                }

            }

            @Override
            public void onFail(int err, String message) {

            }
        });

    }

    private Place extractFromJSON(JSONObject jo) {
        Place place = new Place();
        place.id = jo.optString("id");
        place.name = jo.optString("name");

        JSONObject locationObject = jo.optJSONObject("location");
        if (locationObject != null) {
            place.lat = locationObject.optDouble("lat");
            place.lng = locationObject.optDouble("lng");

            JSONArray addressArray = locationObject.optJSONArray("formattedAddress");
            if (addressArray != null) {
                StringBuilder address  = new StringBuilder();
                StringBuilder shortAdd = new StringBuilder();
                for (int i = 0; i < addressArray.length(); i++) {
                    String value = addressArray.optString(i);
                    address.append(value);
                    if (i != addressArray.length() - 1) {
                        address.append(",");
                    }
                    if (i < 2) {
                        shortAdd.append(value);
                        if (i != 1) {
                            shortAdd.append(",");
                        }
                    }
                }

                place.fullAddress = address.toString();
                place.shortAddress = shortAdd.toString();
            }
        }

        JSONObject categoryObject = jo.optJSONObject("categories");
        if (categoryObject != null) {
            place.categoryName = categoryObject.optString("name");
        }

        return place;
    }

    private String getRequestUrl(String query) {

        StringBuilder builder = new StringBuilder();

        builder.append(C.API_SEARCH)
                .append("client_id=").append(C.FS_CLIENT_ID).append("&")
                .append("client_secret=").append(C.FS_CLIENT_SECRET).append("&")
                .append("v=20191231&")
                .append("limit=10&")
                .append("near=bangalore&");

        if (query != null) {
            builder.append("query=").append(query);
        }

        return builder.toString();

    }

    public interface PlacesFetchListener {

        void onPlacesFetchSuccess(ArrayList<Place> places);

        default void onPlaceFetchFail(String message) {
            Log.e(C.TAG, "Something went wrong");
        }

    }

}
