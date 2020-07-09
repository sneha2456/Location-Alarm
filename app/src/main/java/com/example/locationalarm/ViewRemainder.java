package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ViewRemainder extends AppCompatActivity implements OnMapReadyCallback {

    TextView title,description,distance;
    MapView mapView;
    SwitchMaterial switchRemainder;
    Cursor cursor;
    Databasehelper databasehelper;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng remLocation;
    int remainderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_remainder);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                CalculateDistance(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(ViewRemainder.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewRemainder.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            return;
        }

        else
        {
            locationManager.requestLocationUpdates("gps",1000,1,locationListener);
        }

        title =  (TextView) findViewById(R.id.textViewTitle);
        description = (TextView) findViewById(R.id.textViewDesc);
        distance = (TextView)findViewById(R.id.textViewDist);
        switchRemainder = (SwitchMaterial) findViewById(R.id.switchViewRemainder);

        remainderId = getIntent().getIntExtra("Remid", 99);

        databasehelper = new Databasehelper(ViewRemainder.this);
        cursor = databasehelper.getRemainder(remainderId);

        if(cursor != null)
        {
            cursor.moveToNext();
            title.setText(cursor.getString(1));
            description.setText(cursor.getString(4));

            remLocation= new LatLng(cursor.getDouble(2),cursor.getDouble(3));

            if (cursor.getString(5).equals("true"))
            {
                switchRemainder.setChecked(true);
                title.setTextColor(Color.BLACK);
            }
            else
            {
                switchRemainder.setChecked(false);
                title.setTextColor(Color.rgb(183,187,215));
            }
        }

        switchRemainder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    title.setTextColor(Color.BLACK);
                    databasehelper.updateStatus(remainderId,"true");
                }
                else
                {
                    title.setTextColor(Color.rgb(183,187,215));
                    databasehelper.updateStatus(remainderId,"false");
                }
            }
        });

        mapView = (MapView)findViewById(R.id.mapViewRemainder);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(ViewRemainder.this);
        }
    }

    private void CalculateDistance(Location location) {
        float[] res = new float[1];
        Location.distanceBetween(location.getLatitude(),location.getLongitude(),remLocation.latitude,remLocation.longitude,res);

        distance.setText(Float.toString(res[0]));

        if (res[0]<100.0 && switchRemainder.isChecked() )
        {
            switchRemainder.setChecked(false);
            Intent intent = new Intent(ViewRemainder.this,Notification.class);
            intent.putExtra("Title",title.getText());
            intent.putExtra("Description",description.getText());
            startActivityForResult(intent,12);
        }
    }

    public void onMapReady(GoogleMap googleMap)
    {
        MapsInitializer.initialize(this);
        googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);
        if(ActivityCompat.checkSelfPermission(ViewRemainder.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewRemainder.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(cursor.getDouble(2),cursor.getDouble(3))).title("test"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(cursor.getDouble(2),cursor.getDouble(3))));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 12)
            finish();
    }
}


