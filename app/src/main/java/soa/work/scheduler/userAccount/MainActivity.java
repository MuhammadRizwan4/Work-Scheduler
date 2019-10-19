package soa.work.scheduler.userAccount;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import soa.work.scheduler.CategoryRecyclerViewAdapter;
import soa.work.scheduler.Supportclasses.WeatherIcon;
import soa.work.scheduler.Supportclasses.WeatherText;
import soa.work.scheduler.models.WeatherViewModel_user;
import soa.work.scheduler.utilities.AppStatus;
import soa.work.scheduler.workerAccount.ChooseWorkCategoryActivity;
import soa.work.scheduler.LoginActivity;
import soa.work.scheduler.utilities.PrefManager;
import soa.work.scheduler.R;
import soa.work.scheduler.models.UserAccount;
import soa.work.scheduler.workerAccount.WorkersActivity;
import soa.work.scheduler.models.Category;

import static soa.work.scheduler.data.Constants.PRICE_STARTS_AT;
import static soa.work.scheduler.data.Constants.USER_ACCOUNT;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORK_CATEGORY;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.categories_recycler_view)
    RecyclerView categoriesRecyclerView;
    @BindView(R.id.weather_icon)
    ImageView weather_icon;
    @BindView(R.id.weather_wish)
    TextView weather_wish;
    private ImageView profilePictureImageView;
    private TextView profileNameTextView;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<Category> categories = new ArrayList<>();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4321;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final String IS_ACCOUNT_DELETED = "is_account_deleted";
    public static String city;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean detectCityAutomatically;
    SharedPreferences sharedPrefs;
    public static Activity activity;
    protected WeatherViewModel_user weatherViewModelUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().hasExtra("phone_number")) {
            String phoneNumber = getIntent().getStringExtra("phone_number");
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
        activity = MainActivity.this;

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        detectCityAutomatically = sharedPrefs.getBoolean(
                getString(R.string.detect_city_automatically_key),
                Boolean.valueOf(getString(R.string.detect_city_automatically_default))
        );

        if (detectCityAutomatically) {
            getLocation();
        }

        ButterKnife.bind(this);
        new PrefManager(this).setLastOpenedActivity(USER_ACCOUNT);
        AppStatus appStatus = new AppStatus(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        weatherViewModelUser = ViewModelProviders.of(this).get(WeatherViewModel_user.class);
        weatherViewModelUser.getData().observe(this, weatherModel -> {
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
        getLocationPermission();

        updateAccountSwitcherNavItem();
        setupAccountHeader();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        categories.addAll(Category.getCategories());

        CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(categories, this);
        categoryRecyclerViewAdapter.setItemClickListener(category -> {
            Intent intent = new Intent(this, WorkFormActivity.class);
            intent.putExtra(WORK_CATEGORY, category.getCategoryTitle());
            intent.putExtra(PRICE_STARTS_AT, category.getPrice());
            startActivity(intent);
        });
        categoriesRecyclerView.setAdapter(categoryRecyclerViewAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        categoriesRecyclerView.setLayoutManager(manager);
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
                                WeatherViewModel_user weatherViewModelUser = new WeatherViewModel_user(getApplication());
                                weatherViewModelUser.refresh();
                            }
                        }
                    });
        }
    }

    private void setupAccountHeader() {
        View header = navigationView.getHeaderView(0);
        profilePictureImageView = header.findViewById(R.id.profile_picture_image_view);
        profileNameTextView = header.findViewById(R.id.profile_name_text_view);
        OkHttpClient client = new OkHttpClient();
        Picasso.Builder builder = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client));
        builder.listener((picasso, uri, exception) -> {
            Toast.makeText(MainActivity.this, "Failed to load profile pic", Toast.LENGTH_SHORT).show();
        });
        Picasso pic = builder.build();
        pic.load(currentUser.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(profilePictureImageView);
        profileNameTextView.setText(currentUser.getDisplayName());
    }

    private void updateAccountSwitcherNavItem() {
        Menu navMenu = navigationView.getMenu();
        MenuItem setupWorkerAccountItem = navMenu.findItem(R.id.setup_worker_account_menu_item);
        MenuItem switchToWorkerAccountItem = navMenu.findItem(R.id.switch_to_worker_account_menu_item);
        MenuItem dummyItem = navMenu.findItem(R.id.dummy);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userAccountsRef = firebaseDatabase.getReference(USER_ACCOUNTS);
        DatabaseReference currentAccount = userAccountsRef.child(currentUser.getUid());
        currentAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                dummyItem.setVisible(false);
                if (userAccount == null) {
                    Toast.makeText(MainActivity.this, "Looks like your account has been deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra(IS_ACCOUNT_DELETED, true);
                    startActivity(intent);
                    finish();
                    return;
                }

                if (userAccount.getWorkCategory() == null || userAccount.getWorkCategory().equals("false")) {
                    setupWorkerAccountItem.setVisible(true);
                    switchToWorkerAccountItem.setVisible(false);
                    return;
                }

                if (!userAccount.getWorkCategory().isEmpty() && !userAccount.getWorkCategory().equals("false")) {
                    OneSignal.sendTag(WORK_CATEGORY, userAccount.getWorkCategory());
                    setupWorkerAccountItem.setVisible(false);
                    switchToWorkerAccountItem.setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.dummy:
                return true;
            case R.id.setup_worker_account_menu_item:
                Intent worker = new Intent(MainActivity.this, ChooseWorkCategoryActivity.class);
                startActivity(worker);
                finish();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.switch_to_worker_account_menu_item:
                Intent workerActivity = new Intent(MainActivity.this, WorkersActivity.class);
                startActivity(workerActivity);
                finish();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.history_menu_item:
                Intent history = new Intent(MainActivity.this, WorksHistoryActivity.class);
                startActivity(history);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.settings_menu_item:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
        }
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Snackbar snackbar = Snackbar.make(drawerLayout, "You cannot receive notification without location permission", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Allow Permission", view -> ActivityCompat.requestPermissions(MainActivity.this,
                                permissions,
                                LOCATION_PERMISSION_REQUEST_CODE));
                        snackbar.show();
                        return;
                    }
                }
                sendDeviceLocationToOneSignal();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("phone_number")) {
            String phoneNumber = getIntent().getStringExtra("phone_number");
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(phoneIntent);
        }
    }
}
