package soa.work.scheduler.userAccount;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.R;

import static soa.work.scheduler.data.Constants.LATITUDE;
import static soa.work.scheduler.data.Constants.LOCALITY;
import static soa.work.scheduler.data.Constants.LONGITUDE;

@SuppressWarnings("FieldCanBeLocal")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    @BindView(R.id.drag_result)
    TextView dragResultText;
    @BindView(R.id.done_button)
    FloatingActionButton doneButton;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.ic_gps)
    ImageView map_gps;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private String locality, latitude, longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean hasLocationPermission = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        removeStatusBarTranslucent();
        ButterKnife.bind(this);
        setTitle("Choose location");

        getLocationPermission();

        //Initializing Map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        search();

        //Listener for camera idle state and gets details of location
        onCameraIdleListener = () -> {
            LatLng latLng = mMap.getCameraPosition().target;
            latitude = String.valueOf(latLng.latitude);
            longitude = String.valueOf(latLng.longitude);

            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    locality = addressList.get(0).getAddressLine(0);
                    //String country = addressList.get(0).getCountryName();
                    if (locality != null) {
                        dragResultText.setText(locality);
                    } else {
                        dragResultText.setText("");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        doneButton.setOnClickListener(view -> {
            if (locality != null) {
                Intent intent = new Intent();
                intent.putExtra(LOCALITY, locality);
                intent.putExtra(LONGITUDE, longitude);
                intent.putExtra(LATITUDE, latitude);

                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void search (){
        Places.initialize(getApplicationContext(), "AIzaSyDxlNwc0KqZFIvu-mh2lwxVejeZeXAOsR8");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        }

        // Set up a PlaceSelectionListener to handle the response.
        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    //getLocate(place.getName());
                    AddPlace(place, 1);
                    Log.i("Places", "Place: " + place.getName() + ", " + place.getId());
                }

                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i("Places", "An error occurred: " + status);
                }
            });
        }
        map_gps.setOnClickListener(view -> getDeviceLocation());
    }

//    private void getLocate(String searchString){
//        Geocoder geocoder = new Geocoder((MapsActivity.this));
//        List<Address> list = new ArrayList<>();
//
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (list.size() > 0){
//            Address address = list.get(0);
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), address.getAddressLine(0));
//        }
//
//    }

    public void AddPlace(Place place, int pno) {
        try {
            if (mMap == null) {
                Toast.makeText(MapsActivity.this, "Please check your API key for Google Places SDK and your internet conneciton", Toast.LENGTH_LONG).show();
            } else {

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 4));

                mMap.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng()))
                        .title("Name:" + place.getName() + ". Address:" + place.getAddress()));

            }
        } catch (Exception ex) {

            Toast.makeText(MapsActivity.this, "Error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                hasLocationPermission = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        hasLocationPermission = false;
                        Snackbar snackbar = Snackbar.make(rootView, "Can't get your current location without location permission", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Allow Permission", view -> ActivityCompat.requestPermissions(MapsActivity.this,
                                permissions,
                                LOCATION_PERMISSION_REQUEST_CODE));
                        snackbar.show();
                        return;
                    }
                }
                hasLocationPermission = true;
                getDeviceLocation();
            }
        }
    }


    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (hasLocationPermission) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "My location");
                        mMap.setMyLocationEnabled(true);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapsActivity.DEFAULT_ZOOM));

        if (!title.equals("My location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(onCameraIdleListener);
        if (hasLocationPermission) {
            getDeviceLocation();
        }
    }

    protected void removeStatusBarTranslucent() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

}
