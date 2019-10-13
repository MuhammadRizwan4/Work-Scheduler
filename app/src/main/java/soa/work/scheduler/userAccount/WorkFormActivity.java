package soa.work.scheduler.userAccount;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soa.work.scheduler.R;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.RetrofitClient;
import soa.work.scheduler.utilities.AppStatus;
import soa.work.scheduler.utilities.DatePickerFragment;
import soa.work.scheduler.utilities.OneSignal;
import soa.work.scheduler.utilities.TimePickerFragment;
import soa.work.scheduler.models.IndividualWork;
import soa.work.scheduler.models.OneSignalIds;
import soa.work.scheduler.models.UniversalWork;

import static soa.work.scheduler.data.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.data.Constants.LATITUDE;
import static soa.work.scheduler.data.Constants.LOCALITY;
import static soa.work.scheduler.data.Constants.LONGITUDE;
import static soa.work.scheduler.data.Constants.NEAR_BY_RADIUS;
import static soa.work.scheduler.data.Constants.UID;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKS_POSTED;
import static soa.work.scheduler.data.Constants.WORK_CATEGORY;

@SuppressWarnings("FieldCanBeLocal")
public class WorkFormActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener, TimePickerFragment.TimeDialogListener {

    @BindView(R.id.address_edit_text)
    EditText addressEditText;
    @BindView(R.id.price_range_from)
    EditText priceRangeFromEditText;
    @BindView(R.id.price_range_to)
    EditText priceRangeToEditText;
    @BindView(R.id.phone_number)
    EditText phoneNumberEditText;
    @BindView(R.id.work_description_edit_text)
    EditText workDescriptionEditText;
    @BindView(R.id.picked_date_text_view)
    TextView pickedDateTextView;
    @BindView(R.id.date)
    Button button_date;
    @BindView(R.id.choose_on_maps_button)
    Button chooseOnMapsButton;
    @BindView(R.id.address_edit_text_layout)
    TextInputLayout addressEditTextLayout;
    private AppStatus appStatus;
    private String oneSignalAppId;
    private String oneSignalRestApiKey;
    private String workCategory;
    private static final String DIALOG_DATE = "MainActivity.DateDialog";
    private static final String DIALOG_TIME = "MainActivity.TimeDialog";
    private static final int CHOOSE_ON_MAPS_REQUEST_CODE = 301;
    private String deadline, deadline_date, locality, latitude, longitude;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "WorkFormActivity";
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_form);

        ButterKnife.bind(this);

        workCategory = getIntent().getStringExtra(WORK_CATEGORY);
        addressEditTextLayout.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Broadcasting");
        progressDialog.setCancelable(false);

        addressEditText.setFocusable(false);

        appStatus = new AppStatus(this);
        button_date.setOnClickListener(arg0 -> {
            DatePickerFragment dialog = new DatePickerFragment();
            dialog.show(getSupportFragmentManager(), DIALOG_DATE);
        });

        chooseOnMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!appStatus.isOnline()){
                    Toast.makeText(WorkFormActivity.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isServicesOK()) {
                        Intent intent = new Intent(WorkFormActivity.this, MapsActivity.class);
                        startActivityForResult(intent, CHOOSE_ON_MAPS_REQUEST_CODE);
                    }
                }
            }
        });
    }

    private boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(WorkFormActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(WorkFormActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't choose location on Maps due to some error", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_ON_MAPS_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                locality = data.getStringExtra(LOCALITY);
                longitude = data.getStringExtra(LONGITUDE);
                latitude = data.getStringExtra(LATITUDE);
                addressEditTextLayout.setVisibility(View.VISIBLE);
                addressEditText.setText(locality);
            } else if (data == null){
                Toast.makeText(this, "Address can't be empty.Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void broadcast() {
        //Toast.makeText(this, date1, Toast.LENGTH_SHORT).show();
        if (addressEditText.getText().toString().trim().isEmpty() ||
                phoneNumberEditText.getText().toString().trim().isEmpty() ||
                workDescriptionEditText.getText().toString().trim().isEmpty() ||
                phoneNumberEditText.getText().toString().length() != 10 ||
                deadline == null ||
                deadline.trim().isEmpty() ||
                priceRangeFromEditText.getText().toString().trim().isEmpty() ||
                latitude.trim().isEmpty() ||
                longitude.trim().isEmpty() ||
                priceRangeToEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

        progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Saving work info to currently_available_works
        DatabaseReference currentlyAvailableWorksRef = database.getReference(CURRENTLY_AVAILABLE_WORKS);
        UniversalWork work = new UniversalWork();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        user = FirebaseAuth.getInstance().getCurrentUser();
        work.setAssigned_at("");
        work.setAssigned_to("");
        work.setAssigned_to_id("");
        work.setCreated_date(currentDateAndTime);
        work.setUser_phone(phoneNumberEditText.getText().toString());
        work.setWork_address(addressEditText.getText().toString());
        work.setPrice_range_from(priceRangeFromEditText.getText().toString());
        work.setPrice_range_to(priceRangeToEditText.getText().toString());
        work.setWork_category(workCategory);
        work.setWork_completed(false);
        work.setWork_deadline(deadline);
        work.setWork_description(workDescriptionEditText.getText().toString());
        work.setLatitude(latitude);
        work.setLongitude(longitude);
        if (user != null) {
            work.setWork_posted_by_account_id(user.getUid());
        }
        if (user != null) {
            work.setWork_posted_by_name(user.getDisplayName());
        }
        if (user != null) {
            currentlyAvailableWorksRef.child(user.getUid() + "-" + currentDateAndTime).setValue(work);
        }

        //Saving work info to user's history
        DatabaseReference userAccountsRef = database.getReference(USER_ACCOUNTS);
        IndividualWork individualWork = new IndividualWork();
        individualWork.setWork_category(workCategory);
        individualWork.setWork_description(workDescriptionEditText.getText().toString());
        individualWork.setWork_address(addressEditText.getText().toString());
        individualWork.setUser_phone(phoneNumberEditText.getText().toString());
        individualWork.setPrice_range_from(priceRangeFromEditText.getText().toString());
        individualWork.setPrice_range_to(priceRangeToEditText.getText().toString());
        individualWork.setCreated_date(currentDateAndTime);
        individualWork.setAssigned_to("");
        individualWork.setAssigned_to_id("");
        individualWork.setAssigned_at("");
        individualWork.setWork_completed(false);
        individualWork.setWork_deadline(deadline);
        individualWork.setIs_work_available(true);
        individualWork.setWork_latitude(latitude);
        individualWork.setWork_longitude(longitude);

        if (user != null) {
            userAccountsRef.child(user.getUid()).child(WORKS_POSTED).child(user.getUid() + "-" + currentDateAndTime).setValue(individualWork);
            getOneSignalKeys();
        }
    }

    private void getOneSignalKeys() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("onesignal_id.txt").getDownloadUrl().addOnSuccessListener(uri -> {
            ApiService apiService = RetrofitClient.getApiService();
            Call call = apiService.getOneSignalIds(uri.toString());
            call.enqueue(new Callback<OneSignalIds>() {

                @Override
                public void onResponse(@NonNull Call<OneSignalIds> call, @NonNull Response<OneSignalIds> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        oneSignalAppId = response.body().getOnesignalAppId();
                        oneSignalRestApiKey = response.body().getRestApiKey();
                        String jsonBody = "{" +
                                "\"app_id\": \"" + oneSignalAppId + "\"," +
                                "\"filters\": [" +
                                "{\"field\": \"tag\", \"key\": \"" + WORK_CATEGORY + "\", \"relation\": \"=\", \"value\": \"" + workCategory + "\"}," +
                                "{\"field\": \"location\",\"radius\": \"" + (int) NEAR_BY_RADIUS + "\",\"lat\" : \"" + latitude + "\",\"long\" :\"" + longitude + "\"}," +
                                "{\"field\": \"tag\", \"key\": \"" + UID + "\", \"relation\": \"!=\", \"value\": \"" + user.getUid() + "\"}]," +
                                "\"contents\": {\"en\": \"A new work is available at " + addressEditText.getText().toString() + "\"}" +
                                "}";
                        AsyncTask.execute(() -> OneSignal.sendNotification(oneSignalRestApiKey, jsonBody));
                        Toast.makeText(WorkFormActivity.this, "Broadcast complete", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<OneSignalIds> call, @NonNull Throwable t) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.work_form_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.send_work_menu_item) {
            if (appStatus.isOnline())
                broadcast();
            else
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishDialog(Date date) {
        deadline_date = formatDate(date);
        TimePickerFragment dialog = new TimePickerFragment();
        dialog.show(getSupportFragmentManager(), DIALOG_TIME);
    }

    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onFinishDialog(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
            Date date = sdf.parse(deadline_date + " " + time);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
            if (date != null) {
                deadline = sdf2.format(date);
            }
            pickedDateTextView.setText(deadline.replace("_", " "));
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
        }
    }
}