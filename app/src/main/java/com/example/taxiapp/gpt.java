//package com.example.taxiapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.util.Log;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//public class HomePage extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
//    private Button orderBtn, orderAppBtn, orderCallBtn, backButton;
//    private EditText etStartPoint, etTarget;
//    private GoogleMap mMap;
//    private MarkerOptions place1, place2;
//    private Polyline currentPolyline;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_page);
//
//        // Initialize views
//        initiateViews();
//        // Setup button click listeners
//        setupButtonClickListeners();
//    }
//
//    private void initiateViews() {
//        orderCallBtn = findViewById(R.id.callBtn);
//        orderAppBtn = findViewById(R.id.appBtn);
//        orderBtn = findViewById(R.id.btnFindTaxi);
//        etStartPoint = findViewById(R.id.etStartPoint);
//        etTarget = findViewById(R.id.etTarget);
//        backButton = findViewById(R.id.backButton);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.mapView);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        } else {
//            // Handle the error - the fragment is not found.
//        }
//    }
//
//    private void setupButtonClickListeners() {
//        orderCallBtn.setOnClickListener(v -> showCallPopUp());
//        orderAppBtn.setOnClickListener(v -> showInputFields());
//        orderBtn.setOnClickListener(v -> findRoute());
//        backButton.setOnClickListener(v -> finish());
//    }
//
//    private void findRoute() {
//        String startAddress = etStartPoint.getText().toString();
//        String endAddress = etTarget.getText().toString();
//
//        LatLng startLatLng = getLatLngFromAddress(startAddress);
//        LatLng endLatLng = getLatLngFromAddress(endAddress);
//
//        if (startLatLng != null && endLatLng != null) {
//            place1 = new MarkerOptions().position(startLatLng).title("Start Location");
//            place2 = new MarkerOptions().position(endLatLng).title("End Location");
//
//            mMap.addMarker(place1);
//            mMap.addMarker(place2);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 14));
//
//            new FetchURL(HomePage.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
//        } else {
//            // Handle location not found
//            Log.e("HomePage", "Location not found");
//        }
//    }
//    public void onMapReady(GoogleMap googleMap) {
//        // Add a marker in Sydney, Australia,
//        // and move the map's camera to the same location.
//        mMap = googleMap;
//        // LatLng kiryatGat = new LatLng(31.6100, 34.7642);
//
//        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kiryatGat, 13)); // 10 here is an example zoom level
////        mMap.getUiSettings().setZoomControlsEnabled(true);
//        Log.d("mylog", "Added Markers");
//        mMap.addMarker(place1);
//        mMap.addMarker(place2);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place1.getPosition(), 2));
//
//    }
//
//    // ... (rest of your existing methods, including onMapReady, getUrl, onTaskDone, etc.)
//    private void showCallPopUp() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Call: +1234567890")
//                .setCancelable(true)
//                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent callIntent = new Intent(Intent.ACTION_CALL);
//                        callIntent.setData(Uri.parse("tel:+1234567890"));
//                        startActivity(callIntent);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//    private LatLng getLatLngFromAddress(String address) {
//        Geocoder geocoder = new Geocoder(this);
//        List<Address> addresses;
//        LatLng latLng = null;
//
//        try {
//            addresses = geocoder.getFromLocationName(address, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                Address location = addresses.get(0);
//                latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return latLng;
//    }
//    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
//        // Origin of route
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        // Destination of route
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//        // Mode
//        String mode = "mode=" + directionMode;
//        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + mode;
//        // Output format
//        String output = "json";
//        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
//        return url;
//    }
//    public void onTaskDone(Object... values) {
//        if (currentPolyline != null)
//            currentPolyline.remove();
//        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//
//    // ... (rest of the methods, if any)
//}
