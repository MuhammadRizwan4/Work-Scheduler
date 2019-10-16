package soa.work.scheduler.userAccount;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.R;
import soa.work.scheduler.models.PriceOffer;
import soa.work.scheduler.utilities.AppStatus;
import soa.work.scheduler.utilities.DatePickerFragment;
import soa.work.scheduler.utilities.OneSignal;
import soa.work.scheduler.utilities.TimePickerFragment;
import soa.work.scheduler.models.IndividualWork;
import soa.work.scheduler.models.UniversalWork;

import static soa.work.scheduler.data.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.data.Constants.LATITUDE;
import static soa.work.scheduler.data.Constants.LOCALITY;
import static soa.work.scheduler.data.Constants.LONGITUDE;
import static soa.work.scheduler.data.Constants.NEAR_BY_RADIUS;
import static soa.work.scheduler.data.Constants.PRICE_OFFERS;
import static soa.work.scheduler.data.Constants.PRICE_STARTS_AT;
import static soa.work.scheduler.data.Constants.UID;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKS_POSTED;
import static soa.work.scheduler.data.Constants.WORK_CATEGORY;

@SuppressWarnings("FieldCanBeLocal")
public class WorkFormActivity extends AppCompatActivity implements DatePickerFragment.DateDialogListener, TimePickerFragment.TimeDialogListener {

    @BindView(R.id.address_edit_text)
    EditText addressEditText;
    @BindView(R.id.phone_number_edit_text)
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
    private String workCategory;
    private String workPriceStartsAt;
    private static final String DIALOG_DATE = "MainActivity.DateDialog";
    private static final String DIALOG_TIME = "MainActivity.TimeDialog";
    private static final int CHOOSE_ON_MAPS_REQUEST_CODE = 301;
    private String deadline, deadline_date, locality, latitude, longitude;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_form);

        ButterKnife.bind(this);

        workCategory = getIntent().getStringExtra(WORK_CATEGORY);
        workPriceStartsAt = String.valueOf(getIntent().getDoubleExtra(PRICE_STARTS_AT, 0));
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

        chooseOnMapsButton.setOnClickListener(view -> {
            if (!appStatus.isOnline()){
                Toast.makeText(WorkFormActivity.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            } else {
                if (isServicesOK()) {
                    Intent intent = new Intent(WorkFormActivity.this, MapsActivity.class);
                    startActivityForResult(intent, CHOOSE_ON_MAPS_REQUEST_CODE);
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
                latitude.trim().isEmpty() ||
                longitude.trim().isEmpty()) {
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
        work.setAssignedAt("");
        work.setAssignedTo("");
        work.setAssignedToId("");
        work.setCreatedDate(currentDateAndTime);
        work.setUserPhone(phoneNumberEditText.getText().toString());
        work.setWorkAddress(addressEditText.getText().toString());
        work.setPriceStartsAt(workPriceStartsAt);
        work.setWorkCategory(workCategory);
        work.setWorkCompleted(false);
        work.setWorkDeadline(deadline);
        work.setWorkDescription(workDescriptionEditText.getText().toString());
        work.setLatitude(latitude);
        work.setLongitude(longitude);
        if (user != null) {
            work.setWorkPostedByAccountId(user.getUid());
        }
        if (user != null) {
            work.setWorkPostedByName(user.getDisplayName());
        }
        if (user != null) {
            currentlyAvailableWorksRef.child(user.getUid() + "-" + currentDateAndTime).setValue(work);
        }

        //Saving work info to user's history
        DatabaseReference userAccountsRef = database.getReference(USER_ACCOUNTS);
        IndividualWork individualWork = new IndividualWork();
        individualWork.setWorkCategory(workCategory);
        individualWork.setWorkDescription(workDescriptionEditText.getText().toString());
        individualWork.setWorkAddress(addressEditText.getText().toString());
        individualWork.setUserPhone(phoneNumberEditText.getText().toString());
        individualWork.setCreatedDate(currentDateAndTime);
        individualWork.setAssignedTo("");
        individualWork.setAssignedToId("");
        individualWork.setAssignedAt("");
        individualWork.setPriceStartsAt(workPriceStartsAt);
        individualWork.setWorkCompleted(false);
        individualWork.setWorkDeadline(deadline);
        individualWork.setWorkAvailable(true);
        individualWork.setWorkLatitude(latitude);
        individualWork.setWorkLongitude(longitude);

        if (user != null) {
            userAccountsRef.child(user.getUid()).child(WORKS_POSTED).child(user.getUid() + "-" + currentDateAndTime).setValue(individualWork);

            String jsonBody = "{" +
                    "\"app_id\": \"" + OneSignal.oneSignalAppId + "\"," +
                    "\"filters\": [" +
                    "{\"field\": \"tag\", \"key\": \"" + WORK_CATEGORY + "\", \"relation\": \"=\", \"value\": \"" + workCategory + "\"}," +
                    "{\"field\": \"location\",\"radius\": \"" + (int) NEAR_BY_RADIUS + "\",\"lat\" : \"" + latitude + "\",\"long\" :\"" + longitude + "\"}," +
                    "{\"field\": \"tag\", \"key\": \"" + UID + "\", \"relation\": \"!=\", \"value\": \"" + user.getUid() + "\"}]," +
                    "\"headings\": {\"en\": \"" + user.getDisplayName() + " has posted a new work\"}," +
                    "\"data\": {\"phone_number\": \"" + phoneNumberEditText.getText().toString() + "\"}," +
                    "\"contents\": {\"en\": \"Address: " + addressEditText.getText().toString() + "\"}," +
                    "\"buttons\": [{\"id\": \"call\", \"text\": \"CALL\"}]," +
                    "\"android_accent_color\": \"FF8F00\"" +
                    "}";
            AsyncTask.execute(() -> OneSignal.sendNotification(jsonBody));
            Toast.makeText(WorkFormActivity.this, "Broadcast complete", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog = null;
    }
}
