package com.fa_aanchalbansal_c0846324_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class PlacesActivity extends AppCompatActivity {

    private final ArrayList<PlaceModel> placeList = new ArrayList<>();
    DatabaseManager sqLiteDatabase;
    private RecyclerView rvPlaces;
    private TextView tvTitle;
    private PlacesAdapter placesAdapter;
    private int adapterPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        initViews();
        setAdapter();

        addData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProducts();
    }

    private void loadProducts() {

        placeList.clear();
        Cursor cursor = sqLiteDatabase.getAllPlaces();

        if (cursor.moveToFirst()) {
            do {
                // create an place instance
                placeList.add(new PlaceModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getInt(5)
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        placesAdapter.notifyDataSetChanged();
    }

    private void addData() {
        sqLiteDatabase = new DatabaseManager(this);
        Cursor cursor = sqLiteDatabase.getAllPlaces();
        if (!cursor.moveToFirst()) {
            sqLiteDatabase.addPlace("CN Tower", "290 Bremner Blvd, Toronto, ON M5V 3L9, Canada", 45.70, -79.56, 1);
        }
    }

    private void initViews() {
        rvPlaces = findViewById(R.id.rv_places);
        tvTitle = findViewById(R.id.tv_title);
        findViewById(R.id.iv_back).setVisibility(View.GONE);
        tvTitle.setText("Places");

        findViewById(R.id.fab_add).setOnClickListener(v -> {
            adapterPos = -1;
            startAddPlaceActivity();
        });
    }

    private void setAdapter() {
        placesAdapter = new PlacesAdapter(this, placeList);
        rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        rvPlaces.setAdapter(placesAdapter);

        SwipeController swipeController = new SwipeController(this);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rvPlaces);
    }

    public void openOptions(int adapterPos) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select Option");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        arrayAdapter.add("Edit");
        arrayAdapter.add("Delete");
        arrayAdapter.add("Navigate");

        builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, position) -> {
            switch (position) {
                case 0:
                    this.adapterPos = adapterPos;
                    startAddPlaceActivity();
                    break;
                case 1:
                    sqLiteDatabase.deletePlace(placeList.get(adapterPos).getPlaceId());
                    loadProducts();
                    break;
                case 2:
//                    String uri = String.format(Locale.getDefault(), "geo:%f,%f", placeList.get(adapterPos).getLat(), placeList.get(adapterPos).getLong());
                    String uri ="http://maps.google.com/maps?daddr="+placeList.get(adapterPos).getLat()+","+placeList.get(adapterPos).getLong();
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                    mapIntent.setData(Uri.parse(uri));
                    startActivity(mapIntent);
                    break;
            }
        });
        builderSingle.show();
    }

    private void startAddPlaceActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }
        Intent intent = new Intent(PlacesActivity.this, AddPlaceActivity.class);
        if (adapterPos >= 0)
            intent.putExtra("placeData", placeList.get(adapterPos));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startAddPlaceActivity();
    }

    public void openEditScreen(int adapterPosition) {
        this.adapterPos = adapterPosition;
        startAddPlaceActivity();
    }
}