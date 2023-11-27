package com.example.taxiapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PointsParser {
    private TaskLoadedCallback taskCallback;
    private String directionMode = "driving";
    private ExecutorService executorService;
    private Handler handler;

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
        this.executorService = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void execute(String jsonData) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData);
                    Log.d("mylog", jsonData);
                    DataParser parser = new DataParser();
                    Log.d("mylog", parser.toString());

                    // Starts parsing data
                    routes = parser.parse(jObject);
                    Log.d("mylog", "Executing routes");
                    Log.d("mylog", routes.toString());

                } catch (Exception e) {
                    Log.d("mylog", e.toString());
                    e.printStackTrace();
                }

                List<List<HashMap<String, String>>> finalRoutes = routes;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(finalRoutes);
                    }
                });
            }
        });
    }

    private void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            } else {
                lineOptions.width(20);
                lineOptions.color(Color.BLUE);
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            taskCallback.onTaskDone(lineOptions);
        } else {
            Log.d("mylog", "without Polylines drawn");
        }
    }
}
