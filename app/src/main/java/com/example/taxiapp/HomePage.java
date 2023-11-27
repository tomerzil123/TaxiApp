package com.example.taxiapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private EditText etStartPoint, etTarget;
    private GoogleMap mMap;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.callBtn).setOnClickListener(v -> showCallPopUp());
        findViewById(R.id.btnFindTaxi).setOnClickListener(v -> findRoute());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        etStartPoint = findViewById(R.id.etStartPoint);
        etTarget = findViewById(R.id.etTarget);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("HomePage", "Map fragment not found");
        }
    }

    private void findRoute() {
        String startAddress = etStartPoint.getText().toString();
        String endAddress = etTarget.getText().toString();

        if (validateInput(startAddress, etStartPoint) && validateInput(endAddress, etTarget)) {
            LatLng startLatLng = getLatLngFromAddress(startAddress);
            LatLng endLatLng = getLatLngFromAddress(endAddress);

            if (startLatLng != null && endLatLng != null) {
                updateMapMarkers(startLatLng, endLatLng);
                new FetchURL(this).execute(getUrl(startLatLng, endLatLng), "driving");
            }
        }
        hideKeyboard();
    }

    private boolean validateInput(String input, EditText editText) {
        if (input.isEmpty()) {
            editText.setError("Please enter a location");
            return false;
        }
        return true;
    }

    private void updateMapMarkers(LatLng start, LatLng end) {
        MarkerOptions place1 = new MarkerOptions().position(start).title("Start Location");
        MarkerOptions place2 = new MarkerOptions().position(end).title("End Location");
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 14));
    }

    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            assert addresses != null;
            if (!addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            Log.e("HomePage", "Error getting location", e);
        }
        return null;
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String parameters = str_origin + "&" + str_dest + "&mode=driving";
        return "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Further configuration if needed
    }

    public void onTaskDone(Object... values) {
        if (currentPolyline != null) currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void showCallPopUp() {
        new AlertDialog.Builder(this)
                .setMessage("Call: +1234567890")
                .setCancelable(true)
                .setPositiveButton("Call", (dialog, id) -> startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:+1234567890"))))
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel()) // Replaced with lambda
                .create()
                .show();
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
