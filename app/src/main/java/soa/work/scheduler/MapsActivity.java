package soa.work.scheduler;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static soa.work.scheduler.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.Constants.LATITUDE;
import static soa.work.scheduler.Constants.LOCALITY;
import static soa.work.scheduler.Constants.LONGITUDE;
import static soa.work.scheduler.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.Constants.WORKS_POSTED;
import static soa.work.scheduler.Constants.WORK_CATEGORY;

@SuppressWarnings("FieldCanBeLocal")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.drag_result)
    TextView dragResultText;
    @BindView(R.id.done_button)
    FloatingActionButton doneButton;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
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
    private FirebaseUser currentUser;

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


        //Listener for camera idle state and gets details of location
        onCameraIdleListener = () -> {
            LatLng latLng = mMap.getCameraPosition().target;
            latitude = String.valueOf(latLng.latitude);
            longitude = String.valueOf(latLng.longitude);

            Toast.makeText(this, latitude, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, longitude, Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent();
            intent.putExtra(LOCALITY, locality);
            intent.putExtra(LONGITUDE, longitude);
            intent.putExtra(LATITUDE, latitude);

            setResult(RESULT_OK, intent);
            finish();
        });

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
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        mMap.setMyLocationEnabled(true);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
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
