package com.fa_aanchalbansal_c0846324_android;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    double latitude = 0;
    double longitude = 0;
    float zoomLevel = (float) 17.0;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private GoogleMap googleMap;
    private EditText etName;
    private TextView tvAddress;
    private CheckBox cbVisited;
    private int placeId = -1;
    private Button btnSave;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);


        etName = findViewById(R.id.et_name);
        tvAddress = findViewById(R.id.txt_location);
        cbVisited = findViewById(R.id.cb_mark_visited);
        btnSave = findViewById(R.id.btn_save_address);


        Places.initialize(getApplicationContext(), String.valueOf(getString(R.string.google_api_key)));

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        View mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        configureCameraIdle();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                if (latitude != 0) {
//                   // LatLng latLng = new LatLng(latitude, longitude);
//                   // googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
//                }

            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnCameraIdleListener(onCameraIdleListener);
        if (latitude != 0) {
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        } else {
            googleMap.setMyLocationEnabled(true);
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                latitude = latlng.latitude;
                longitude = latlng.longitude;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
            }
        });
        findViewById(R.id.txt_change).setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
        findViewById(R.id.btn_normal).setOnClickListener(v -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        });
        findViewById(R.id.btn_hybrid).setOnClickListener(v -> {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        });
        findViewById(R.id.imv_back).setOnClickListener(v -> {
            onBackPressed();
        });
        btnSave.setOnClickListener(v -> {
            if (latitude != 0 && longitude != 0 && !etName.getText().toString().isEmpty()) {
                DatabaseManager sqLiteDatabase = new DatabaseManager(this);
                if (placeId == -1) {
                    sqLiteDatabase.addPlace(
                            etName.getText().toString().trim(),
                            tvAddress.getText().toString().trim(),
                            latitude, longitude,
                            cbVisited.isChecked() ? 1 : 0
                    );
                } else {
                    sqLiteDatabase.updatePlace(
                            placeId,
                            etName.getText().toString().trim(),
                            tvAddress.getText().toString().trim(),
                            latitude, longitude,
                            cbVisited.isChecked() ? 1 : 0
                    );
                }
                onBackPressed();
            } else {
                Toast.makeText(AddPlaceActivity.this, "Invalid Location or Title", Toast.LENGTH_LONG).show();
            }
        });
        if (getIntent() != null && getIntent().getExtras() != null) {
            PlaceModel placeModel = (PlaceModel) getIntent().getSerializableExtra("placeData");
            tvAddress.setText(placeModel.getAddress());
            etName.setText(placeModel.getTitle());
            etName.setSelection(etName.getText().toString().length());
            latitude = placeModel.getLat();
            longitude = placeModel.getLong();
            placeId = placeModel.getPlaceId();
            cbVisited.setChecked(placeModel.getVisited() == 1);
            btnSave.setText("Update");
            if (latitude != 0) {
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            }
        }
    }


    private void configureCameraIdle() {
        onCameraIdleListener = () -> {
            LatLng latLng = googleMap.getCameraPosition().target;
            Geocoder geocoder = new Geocoder(AddPlaceActivity.this);
            latitude = latLng.latitude;
            longitude = latLng.longitude;

            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    String addr = "";
                    if (addressList.get(0).getSubLocality() != null && !addressList.get(0).getSubLocality().isEmpty()) {
                        addr += addressList.get(0).getSubLocality();
                    }
                    if (addressList.get(0).getLocality() != null && !addressList.get(0).getLocality().isEmpty()) {
                        if (!addr.trim().isEmpty()) addr += ", ";
                        addr += addressList.get(0).getLocality();
                    }
                    ((TextView) AddPlaceActivity.this.findViewById(R.id.tv_lable)).setText(addr);
                    if (addressList.get(0).getAddressLine(0) != null && !addressList.get(0).getAddressLine(0).isEmpty()) {
                        addr = addressList.get(0).getAddressLine(0);
                    }
                    ((TextView) AddPlaceActivity.this.findViewById(R.id.txt_location)).setText(addr);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                if (latitude != 0) {
                    LatLng latLng = new LatLng(latitude, longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
