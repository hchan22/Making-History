package nyc.c4q.helenchan.makinghistory;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreMoreActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference mFirebaseDatabase;

    private static final String TAG = "Main Activity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mLocationClient;
    GoogleMap mMap;
    private float zoomLevel = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (checkPlayServices()) {
            setContentView(R.layout.activity_map);

            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mLocationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mLocationClient.connect();
        }

        Button searchAddressBtn = (Button) findViewById(R.id.location_search_btn);
        searchAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    locateFromAddress(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mMap.moveCamera(update);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(checkPermissions()){
            mMap.setMyLocationEnabled(true);
        } else if (requestPermissions()){
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "To view your location, please visit settings and change location permissions", Toast.LENGTH_LONG).show();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map timestampMap = dataSnapshot.getChildren();
                String timestamp2 = dataSnapshot.getChildren().child("timestamp").getValue().toString();
                Log.d("got a timestamp", timestamp2);

//                String timestamp = (String) data.get("timestamp");
//
//                Map mCoordinate = (HashMap) data.get("location");
//                double latitude = (double) (mCoordinate.get("latitude"));
//                double longitude = (double) (mCoordinate.get("longitude"));
//                LatLng mLatlng = new LatLng(latitude, longitude);
//                MarkerOptions mMarkerOption = new MarkerOptions()
//                        .position(mLatlng)
//                        .title(timestamp)
//                        .icon(BitmapDescriptorFactory.defaultMarker());
//                Marker mMarker = mMap.addMarker(mMarkerOption);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        LatLng googleHQ = new LatLng(40.741815, -74.004230);
        mMap.addMarker(new MarkerOptions().position(googleHQ).title("Marker at Google HQ"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(googleHQ));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel), 5000, null);
    }


    private void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void locateFromAddress(View v) throws IOException {
        hideSoftKeyboard(v);

        EditText locationInput = (EditText) findViewById(R.id.location_search_input);
        String searchString = locationInput.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            gotoLocation(lat, lng, zoomLevel);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Map working!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Map creation interrupted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Map not connected!!", Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private boolean requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
        return checkPermissions();
    }

}
