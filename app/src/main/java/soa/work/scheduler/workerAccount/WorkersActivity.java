package soa.work.scheduler.workerAccount;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.LoginActivity;
import soa.work.scheduler.R;
import soa.work.scheduler.Supportclasses.WeatherIcon;
import soa.work.scheduler.Supportclasses.WeatherText;
import soa.work.scheduler.models.WeatherViewModel_user;
import soa.work.scheduler.models.WeatherViewModel_worker;
import soa.work.scheduler.utilities.AppStatus;
import soa.work.scheduler.utilities.PrefManager;
import soa.work.scheduler.models.UniversalWork;
import soa.work.scheduler.models.UserAccount;
import soa.work.scheduler.userAccount.MainActivity;

import static soa.work.scheduler.data.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.data.Constants.NEAR_BY_RADIUS;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKER_ACCOUNT;
import static soa.work.scheduler.data.Constants.WORK_CATEGORY;
import static soa.work.scheduler.userAccount.MainActivity.IS_ACCOUNT_DELETED;

@SuppressWarnings("FieldCanBeLocal")
public class WorkersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WorksAvailableAdapter.ItemCLickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.weather_icon)
    ImageView weather_icon;
    @BindView(R.id.weather_wish)
    TextView weather_wish;
    private String work_category;
    private FirebaseUser firebaseUser;
    private ImageView profilePictureImageView;
    private TextView profileNameTextView;
    private AppStatus appStatus;
    private WorksAvailableAdapter worksAvailableAdapter;
    private ArrayList<UniversalWork> workList = new ArrayList<>();
    @BindView(R.id.works_recycler_view)
    RecyclerView worksRecyclerView;
    @BindView(R.id.no_history)
    TextView noWorksTextView;
    private ProgressDialog progressDialog;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4321;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLatitude;
    private double currentLongitude;
    protected WeatherViewModel_worker weatherViewModelWorker;
    public static Activity activity;
    public static String city;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean detectCityAutomatically;
    SharedPreferences sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers);

        ButterKnife.bind(this);

        activity = WorkersActivity.this;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        detectCityAutomatically = sharedPrefs.getBoolean(
                getString(R.string.detect_city_automatically_key),
                Boolean.valueOf(getString(R.string.detect_city_automatically_default))
        );

        if (detectCityAutomatically) {
            getLocation();
        }

        new PrefManager(this).setLastOpenedActivity(WORKER_ACCOUNT);

        appStatus = new AppStatus(this);
        setTitle("Worker Account");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        weatherViewModelWorker = ViewModelProviders.of(this).get(WeatherViewModel_worker.class);
        weatherViewModelWorker.getData().observe(this, weatherModel -> {
            String iconId = weatherModel.getWeather().get(0).getIcon();

            if (weatherModel != null) {
                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
                if(timeOfDay < 12){
                    weather_wish.setText("Good Morning " + acct.getGivenName() + ", " + WeatherText.getWeatherText(iconId));
                }else if(timeOfDay < 16){
                    weather_wish.setText("Good Afternoon " + acct.getGivenName() + ", " + WeatherText.getWeatherText(iconId));
                }else if(timeOfDay < 23){
                    weather_wish.setText("Good Evening " + acct.getGivenName() + ", " + WeatherText.getWeatherText(iconId));
                }else {
                    weather_wish.setText("Good Night " + acct.getGivenName() + ", " + WeatherText.getWeatherText(iconId));
                }


                weather_icon.setImageResource(WeatherIcon.getWeatherIcon(iconId));
            } else {
                if (appStatus.isOnline()) {
                    weather_wish.setText("Unable to load");
                    weather_icon.setImageResource(R.drawable.unknown);
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userAccountsRef = FirebaseDatabase.getInstance().getReference(USER_ACCOUNTS);
        DatabaseReference currentAccount = null;
        if (currentUser != null) {
            currentAccount = userAccountsRef.child(currentUser.getUid());
        }
        currentAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                if (userAccount == null) {
                    Toast.makeText(WorkersActivity.this, "Looks like your account has been deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorkersActivity.this, LoginActivity.class);
                    intent.putExtra(IS_ACCOUNT_DELETED, true);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getLocationPermission();

        if (!appStatus.isOnline()) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else {
            worksAvailableAdapter = new WorksAvailableAdapter(workList, this);
            worksAvailableAdapter.setItemClickListener(this);
            worksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            worksRecyclerView.setHasFixedSize(true);
            worksRecyclerView.setAdapter(worksAvailableAdapter);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            View header = navigationView.getHeaderView(0);
            profilePictureImageView = header.findViewById(R.id.profile_picture_image_view);
            profileNameTextView = header.findViewById(R.id.profile_name_text_view);
            Picasso.get().load(firebaseUser.getPhotoUrl()).into(profilePictureImageView);
            profileNameTextView.setText(firebaseUser.getDisplayName());

            setSupportActionBar(toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference workCategoryRef = database.getReference(USER_ACCOUNTS).child(firebaseUser.getUid()).child(WORK_CATEGORY);
            DatabaseReference currently_available = database.getReference().child(CURRENTLY_AVAILABLE_WORKS);

            workCategoryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    work_category = dataSnapshot.getValue(String.class);
                    OneSignal.sendTag(WORK_CATEGORY, work_category);
                    currently_available.orderByChild(WORK_CATEGORY)
                            .equalTo(work_category)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    workList.clear();
                                    progressDialog.dismiss();
                                    if (dataSnapshot.getChildrenCount() == 0) {
                                        noWorksTextView.setVisibility(View.VISIBLE);
                                        worksAvailableAdapter.notifyDataSetChanged();
                                        return;
                                    }
                                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                                        UniversalWork work = item.getValue(UniversalWork.class);
                                        if (work.getAssignedAt().isEmpty() && work.getAssignedTo().isEmpty()) {
                                            if (!work.getWorkPostedByAccountId().equals(firebaseUser.getUid())) {
                                                if (work.getLatitude() == null || work.getLatitude().isEmpty()) {
                                                    continue;
                                                }
                                                if (work.getLongitude() == null || work.getLongitude().isEmpty()) {
                                                    continue;
                                                }
                                                double distance = distanceBetweenCoordinates(Double.parseDouble(work.getLatitude()), currentLatitude, Double.parseDouble(work.getLongitude()), currentLongitude);

                                                if (distance <= NEAR_BY_RADIUS) {
                                                    workList.add(work);
                                                }
                                            }
                                        } else {
                                            if (work.getAssignedToId().equals(firebaseUser.getUid())) {
                                                if (work.getLatitude() == null || work.getLatitude().isEmpty()) {
                                                    continue;
                                                }
                                                if (work.getLongitude() == null || work.getLongitude().isEmpty()) {
                                                    continue;
                                                }
                                                double distance = distanceBetweenCoordinates(Double.parseDouble(work.getLatitude()), currentLatitude, Double.parseDouble(work.getLongitude()), currentLongitude);

                                                if (distance <= NEAR_BY_RADIUS) {
                                                    workList.add(work);
                                                }
                                            }
                                        }
                                    }
                                    if (workList.isEmpty()) {
                                        noWorksTextView.setVisibility(View.VISIBLE);
                                        worksAvailableAdapter.notifyDataSetChanged();
                                        return;
                                    }

                                    Collections.sort(workList, new Comparator<UniversalWork>() {
                                        @Override
                                        public int compare(UniversalWork individualWork, UniversalWork t1) {
                                            return individualWork.getCreatedDate().compareTo(t1.getCreatedDate());
                                        }
                                    });
                                    Collections.reverse(workList);
                                    noWorksTextView.setVisibility(View.GONE);
                                    worksAvailableAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(WorkersActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            Geocoder gcd = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addresses != null && addresses.size() > 0) {
                                city = addresses.get(0).getLocality();
                                WeatherViewModel_worker weatherViewModelWorker = new WeatherViewModel_worker(getApplication());
                                weatherViewModelWorker.refresh();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.switch_to_user_account_menu_item:
                Intent mainActivity = new Intent(WorkersActivity.this, MainActivity.class);
                startActivity(mainActivity);
                return true;
            case R.id.settings_menu_item:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(WorkersActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
        }
    }

    @Override
    public void onItemClick(UniversalWork work) {
        Intent intent = new Intent(this, WorkDetailsActivity.class);
        intent.putExtra("created_date", work.getCreatedDate());
        intent.putExtra("work_posted_by_account_id", work.getWorkPostedByAccountId());
        startActivity(intent);
    }

    public static double distanceBetweenCoordinates(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                //We have location permission
                sendDeviceLocationToOneSignal();

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

    private void sendDeviceLocationToOneSignal() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    OneSignal.sendTag("longitude", String.valueOf(currentLocation.getLongitude()));
                    OneSignal.sendTag("latitude", String.valueOf(currentLocation.getLatitude()));
                    currentLatitude = currentLocation.getLatitude();
                    currentLongitude = currentLocation.getLongitude();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
