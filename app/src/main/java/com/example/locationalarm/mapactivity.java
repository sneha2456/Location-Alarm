package com.example.locationalarm;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mapactivity extends AppCompatActivity implements OnMapReadyCallback {

    String taskName, taskDescription ;
    Databasehelper databasehelper;
    Button setRemainder;
    LatLng latLng;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SearchView searchView;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;

    private RippleBackground ripplebg;

    private final float DEFAULT_ZOOM=18;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;
    private static final String TAG = mapactivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapactivity);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_top);
        toolbar.setTitle("SELECT LOCATION");
        //toolbar.setNavigationIcon(R.drawable.quantum_ic_arrow_back_grey600_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),enter_details.class));
            }
        });


        Intent intent = getIntent();
        taskName = intent.getStringExtra("Col_2");
        taskDescription = intent.getStringExtra("Col_5");
        databasehelper = new Databasehelper(this);


        searchView = findViewById(R.id.sv_location);
        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(mapactivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
       // materialSearchBar= findViewById(R.id.searchBar);
        btnFind=findViewById(R.id.btn_find);
        ripplebg =findViewById(R.id.ripple_bg);


        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView=mapFragment.getView();

        mFusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(mapactivity.this);
        Places.initialize(mapactivity.this,"AIzaSyD59_lusEb09NvCVkkp5d3Y8Keovo_3tG0");
        placesClient=Places.createClient(this);
        final AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();

//        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//                startSearch(text.toString(),true,null,true);
//            }
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//                if(buttonCode==MaterialSearchBar.BUTTON_NAVIGATION){
//                    //opening or closing navigation bar
//
//                }else if(buttonCode==MaterialSearchBar.BUTTON_BACK){
//                    materialSearchBar.disableSearch();
//                }
//            }
//        });

        //for search bar suggestion

//        Button searchButton = findViewById(R.id.searchButton);
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
//                    // Start the autocomplete intent.
//                    Intent intent = new Autocomplete.IntentBuilder(
//                            AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //india
//                            .build(mapactivity.this);
//                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
////                    startActivity(new Intent(mapactivity.this,place_prediction_programmatically.class));
//                }catch (Exception e){
//                    Toast.makeText(mapactivity.this,"found error"+e,Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//        materialSearchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                final FindAutocompletePredictionsRequest predictionsRequest=FindAutocompletePredictionsRequest.builder()
//                        .setTypeFilter(TypeFilter.ADDRESS)
//                        .setSessionToken(token)
//                        .setQuery(s.toString())
//                        .build();
//                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
//                        if(task.isSuccessful()){
//                            FindAutocompletePredictionsResponse predictionsResponse=task.getResult();
//                            if (predictionsResponse!=null){
//                                predictionList = predictionsResponse.getAutocompletePredictions();
//                                List<String> suggestionList= new ArrayList<>();
//                                for(int i=0;i<predictionList.size();i++){
//                                    AutocompletePrediction prediction=predictionList.get(i);
//                                    suggestionList.add(prediction.getFullText(null).toString());
//                                }
//                                materialSearchBar.updateLastSuggestions(suggestionList);
//                                if(materialSearchBar.isSuggestionsVisible()){
//                                    materialSearchBar.showSuggestionsList();
//                                }
//                            }
//                        }else {
//                            Log.i("mytag","prediction fetching task unsuccessful");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

//        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
//            @Override
//            public void OnItemClickListener(int position, View v) {
//                if(position>=predictionList.size()){
//                    return;
//                }
//                AutocompletePrediction selectedPrediction =predictionList.get(position);
//                String suggestion= materialSearchBar.getLastSuggestions().get(position).toString();
//                materialSearchBar.setText(suggestion);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        materialSearchBar.clearSuggestions();
//
//                    }
//                },1000);
//
//                InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                if (imm!=null)
//                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                String placeId=selectedPrediction.getPlaceId();
//                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
//
//                final FetchPlaceRequest fetchPlaceRequest=FetchPlaceRequest.builder(placeId, placeFields).build();
//                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//                    @Override
//                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
//                       Place place=fetchPlaceResponse.getPlace();
//                       Log.i("mytag","Place found: " +place.getName());
//                       LatLng latlonOfPlace =place.getLatLng();
//                       if (latlonOfPlace!=null){
//                           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlonOfPlace,DEFAULT_ZOOM));
//                       }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if (e instanceof ApiException){
//                            ApiException apiException=(ApiException) e;
//                            apiException.printStackTrace();
//                            int statusCode=apiException.getStatusCode();
//                            Log.i("mytag","place not found:"+ e.getMessage());
//                            Log.i("mytag","status code: "+ statusCode);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void OnItemDeleteListener(int position, View v) {
//
//            }
//        });
        //to add data in database
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng currentMarkerLocation=mMap.getCameraPosition().target;
                ripplebg.startRippleAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ripplebg.stopRippleAnimation();
                        startActivity(new Intent(mapactivity.this,HomePage.class));
                        finish();
                    }
                },3000);

                boolean res = databasehelper.insertData(taskName,latLng.latitude,latLng.longitude,taskDescription);

                if(res == true)
                {
                    Toast.makeText(mapactivity.this,"Data inserted",Toast.LENGTH_SHORT).show();
                    setResult(1);
                }
                else
                {
                    Toast.makeText(mapactivity.this,"Data not inserted",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.options_menu,menu);
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.search:
//                onSearchCalled();
//                return true;
//            case android.R.id.home:
//                finish();
//                return true;
//            default:
//                return false;
//        }
//    }

//    public void onSearchCalled() {
//        // Set the fields to specify which types of place data to return.
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
//        // Start the autocomplete intent.
//        Intent intent = new Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //india
//                .build(this);
//        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//    }

//dont take this
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
//                Toast.makeText(mapactivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
//                String address = place.getAddress();
//                // do query with address
//
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Toast.makeText(mapactivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
//                Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView!=null && mapView.findViewById(Integer.parseInt("1"))!=null){
            View locationButton=((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,180);
        }
        //check if gps is enabled or not and request it
        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient= LocationServices.getSettingsClient(mapactivity.this);
        Task<LocationSettingsResponse> task=settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(mapactivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(mapactivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              if(e instanceof ResolvableApiException){
                  ResolvableApiException resovlable=(ResolvableApiException) e;
                  try {
                      resovlable.startResolutionForResult(mapactivity.this,51);
                  } catch (IntentSender.SendIntentException ex) {
                      ex.printStackTrace();
                  }
              }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled())
                    materialSearchBar.disableSearch();
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==51){
            if (resultCode==RESULT_OK){
                getDeviceLocation();
            }
        }
//        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
//                Toast.makeText(mapactivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
//                String address = place.getAddress();
//                // do query with address
//
//            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
//                // TODO: Handle the error.
//                Status status = Autocomplete.getStatusFromIntent(data);
//                Toast.makeText(mapactivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
//                Log.i(TAG, status.getStatusMessage());
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
             if (task.isSuccessful()) {
                 mLastKnownLocation = task.getResult();
                 if (mLastKnownLocation != null) {
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                 } else {
                     LocationRequest locationRequest = LocationRequest.create();
                     locationRequest.setInterval(10000);
                     locationRequest.setFastestInterval(5000);
                     locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                     locationCallback = new LocationCallback() {
                         @Override
                         public void onLocationResult(LocationResult locationResult) {
                             super.onLocationResult(locationResult);
                             if (locationResult == null) {
                                 return;
                             }
                             mLastKnownLocation = locationResult.getLastLocation();
                             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                         }
                     };
                     mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                 }
             }else{
                 Toast.makeText(mapactivity.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
             }
            }
        });

    }
}
