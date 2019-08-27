package com.yuluassignment.misc;

import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yuluassignment.C;
import com.yuluassignment.MyApp;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkManager {

    private static NetworkManager manager;
    private        RequestQueue   queue;

    private NetworkManager() {
        queue = Volley.newRequestQueue(MyApp.get());
    }

    public static NetworkManager get() {
        if (manager == null) {
            manager = new NetworkManager();
        }
        return manager;
    }

    public void makeGETRequest(String url, RequestListener listener) {

        StringRequest request = new StringRequest(url,
                response -> {

                    Log.i(C.TAG, response);
                    try {
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("response");
                        listener.onSuccess(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFail(1, "Error parsing response");
                    }

                },
                error -> {
                    String errMess = String.valueOf(error);
                    Log.e(C.TAG, errMess);
                    listener.onFail(1, "Something went wrong: " + errMess);
                }
        );

        queue.add(request);

    }

    public interface RequestListener {

        void onSuccess(JSONObject jsonResponse);

        void onFail(int err, String message);

    }

}
