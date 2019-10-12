package soa.work.scheduler.userAccount;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.R;

import static soa.work.scheduler.data.Constants.LATITUDE;
import static soa.work.scheduler.data.Constants.LOCALITY;
import static soa.work.scheduler.data.Constants.LONGITUDE;

@SuppressWarnings("FieldCanBeLocal")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.drag_result)
    TextView dragResultText;
    @BindView(R.id.done_button)
    FloatingActionButton doneButton;
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    @BindView(R.id.input_search)
    AutoCompleteTextView input_search;
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
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;

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
        input_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH || actionID == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                    keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    getLocate();
                }

                return false;
            }
        });
        hideSoftKeyboard();

        map_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
    }

    private void getLocate(){
        String searchString = input_search.getText().toString();
        Geocoder geocoder = new Geocoder((MapsActivity.this));
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() > 0){
            Address address = list.get(0);

           // Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
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
                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My location");
                        mMap.setMyLocationEnabled(true);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My location")) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();
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

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
