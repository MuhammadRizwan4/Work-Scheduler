package soa.work.scheduler.userAccount;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.CategoryRecyclerViewAdapter;
import soa.work.scheduler.workerAccount.ChooseWorkCategoryActivity;
import soa.work.scheduler.LoginActivity;
import soa.work.scheduler.utilities.PrefManager;
import soa.work.scheduler.R;
import soa.work.scheduler.models.UserAccount;
import soa.work.scheduler.workerAccount.WorkersActivity;
import soa.work.scheduler.models.Category;

import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PLUMBER;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        ButterKnife.bind(this);
        new PrefManager(this).setLastOpenedActivity(USER_ACCOUNT);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        getLocationPermission();

        updateAccountSwitcherNavItem();
        setupAccountHeader();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        categories.add(new Category(MECHANIC, R.drawable.ic_mechanic));
        categories.add(new Category(PLUMBER, R.drawable.ic_plumber));
        categories.add(new Category(ELECTRICIAN, R.drawable.ic_electrician));
        categories.add(new Category(CARPENTER, R.drawable.ic_carpenter));
        categories.add(new Category(PAINTER, R.drawable.ic_painter));

        CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(categories);
        categoryRecyclerViewAdapter.setItemClickListener(category -> {
            Intent intent = new Intent(this, WorkFormActivity.class);
            intent.putExtra(WORK_CATEGORY, category.getCategoryTitle());
            startActivity(intent);
        });
        categoriesRecyclerView.setAdapter(categoryRecyclerViewAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        categoriesRecyclerView.setLayoutManager(manager);
    }

    private void setupAccountHeader() {
        View header = navigationView.getHeaderView(0);
        profilePictureImageView = header.findViewById(R.id.profile_picture_image_view);
        profileNameTextView = header.findViewById(R.id.profile_name_text_view);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Toast.makeText(MainActivity.this, "Failed to load profile pic", Toast.LENGTH_SHORT).show();
            }
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

                if (userAccount.getWork_category() == null || userAccount.getWork_category().equals("false")) {
                    setupWorkerAccountItem.setVisible(true);
                    switchToWorkerAccountItem.setVisible(false);
                    return;
                }

                if (!userAccount.getWork_category().isEmpty() && !userAccount.getWork_category().equals("false")) {
                    OneSignal.sendTag(WORK_CATEGORY, userAccount.getWork_category());
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
        } catch (SecurityException e) {
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
}
