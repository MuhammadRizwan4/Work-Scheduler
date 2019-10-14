package soa.work.scheduler.workerAccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.R;
import soa.work.scheduler.models.UniversalWork;
import soa.work.scheduler.models.UserAccount;
import soa.work.scheduler.utilities.OneSignal;

import static soa.work.scheduler.data.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.data.Constants.UID;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKER_PHONE_NUMBER;
import static soa.work.scheduler.data.Constants.WORKS_POSTED;
import static soa.work.scheduler.data.Constants.WORK_ASSIGNED_AT;
import static soa.work.scheduler.data.Constants.WORK_ASSIGNED_TO;
import static soa.work.scheduler.data.Constants.WORK_ASSIGNED_TO_ID;

public class WorkDetailsActivity extends AppCompatActivity {

    @BindView(R.id.work_accept)
    Button acceptWorkButton;
    @BindView(R.id.posted_at_text_view)
    EditText postedAtTextView;
    @BindView(R.id.posted_by_text_view)
    EditText postedByTextView;
    @BindView(R.id.price_range_text_view)
    EditText priceRangeTextView;
    @BindView(R.id.user_phone_number)
    EditText userPhoneNumberTextView;
    @BindView(R.id.user_location)
    EditText userLocationTextView;
    @BindView(R.id.deadline_text_view)
    EditText deadlineTextView;
    @BindView(R.id.work_description_text_view)
    EditText workDescriptionTextView;
    @BindView(R.id.call_icon)
    ImageView call_user;

    private FirebaseUser currentUser;
    private String created_date, work_posted_by_account_id;
    private boolean isAssigned;
    private String userPhoneNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_details);

        ButterKnife.bind(this);

        setTitle("Work Details");

        postedAtTextView.setFocusable(false);
        postedByTextView.setFocusable(false);
        priceRangeTextView.setFocusable(false);
        userPhoneNumberTextView.setFocusable(false);
        userLocationTextView.setFocusable(false);
        deadlineTextView.setFocusable(false);
        workDescriptionTextView.setFocusable(false);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            created_date = (String) bundle.get("created_date");
            work_posted_by_account_id = (String) bundle.get("work_posted_by_account_id");
        }
        DatabaseReference currentWork = database.getReference(CURRENTLY_AVAILABLE_WORKS).child(work_posted_by_account_id + "-" + created_date);
        DatabaseReference individualWork = database.getReference(USER_ACCOUNTS).child(work_posted_by_account_id).
        child(WORKS_POSTED).child(work_posted_by_account_id + "-" + created_date);
        currentWork.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UniversalWork work = dataSnapshot.getValue(UniversalWork.class);
                postedAtTextView.setText(Objects.requireNonNull(work).getCreatedDate());
                postedByTextView.setText(work.getWorkPostedByName());
                priceRangeTextView.setText("Starts at Rs." + work.getPriceStartsAt());
                userPhoneNumberTextView.setText(work.getUserPhone());
                userLocationTextView.setText(work.getWorkAddress());
                deadlineTextView.setText(work.getWorkDeadline());
                userPhoneNum = work.getUserPhone();
                workDescriptionTextView.setText(work.getWorkDescription());
                if (work.getAssignedToId().equals(currentUser.getUid())) {
                    acceptWorkButton.setText("CANCEL WORK");
                    acceptWorkButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                    isAssigned = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        acceptWorkButton.setOnClickListener(view -> {
            if (!isAssigned) {
                new AlertDialog.Builder(WorkDetailsActivity.this)
                        .setMessage("Are you sure want to accept?")
                        .setCancelable(true)
                        .setPositiveButton("YES", (dialog, which) -> {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference userAccountsRef = firebaseDatabase.getReference(USER_ACCOUNTS);
                            DatabaseReference currentAccount = userAccountsRef.child(currentUser.getUid());
                            currentAccount.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                                    Toast.makeText(WorkDetailsActivity.this, "Accepted", Toast.LENGTH_SHORT).show();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                                    String currentDateAndTime = sdf.format(new Date());

                                    currentWork.child(WORK_ASSIGNED_TO).setValue(currentUser.getDisplayName());
                                    currentWork.child(WORK_ASSIGNED_AT).setValue(currentDateAndTime);
                                    currentWork.child(WORK_ASSIGNED_TO_ID).setValue(currentUser.getUid());
                                    currentWork.child(WORKER_PHONE_NUMBER).setValue(Objects.requireNonNull(userAccount).getPhoneNumber());

                                    DatabaseReference accountOfUser = database.getReference(USER_ACCOUNTS).child(work_posted_by_account_id);
                                    DatabaseReference workInUserHistory = accountOfUser.child(WORKS_POSTED).child(work_posted_by_account_id + "-" + created_date);
                                    workInUserHistory.child(WORK_ASSIGNED_TO).setValue(currentUser.getDisplayName());
                                    workInUserHistory.child(WORK_ASSIGNED_AT).setValue(currentDateAndTime);
                                    workInUserHistory.child(WORK_ASSIGNED_TO_ID).setValue(currentUser.getUid());
                                    workInUserHistory.child(WORKER_PHONE_NUMBER).setValue(userAccount.getPhoneNumber());
                                    acceptWorkButton.setText("CANCEL WORK");
                                    acceptWorkButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                                    isAssigned = true;

                                    AsyncTask.execute(() -> sendNotificationForWorkAccepted());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        })
                        .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).create().show();
            } else {
                new AlertDialog.Builder(WorkDetailsActivity.this)
                        .setMessage("Are you sure want to cancel?")
                        .setCancelable(true)
                        .setPositiveButton("YES", (dialog, which) -> {

                            currentWork.child(WORK_ASSIGNED_TO_ID).setValue("");
                            currentWork.child(WORK_ASSIGNED_TO).setValue("");
                            currentWork.child(WORK_ASSIGNED_AT).setValue("");
                            individualWork.child(WORK_ASSIGNED_TO_ID).setValue("");
                            individualWork.child(WORK_ASSIGNED_TO).setValue("");
                            individualWork.child(WORK_ASSIGNED_AT).setValue("");
                            acceptWorkButton.setText("ACCEPT THIS WORK");
                            acceptWorkButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

                            AsyncTask.execute(this::sendNotificationForWorkCancelled);
                        })
                        .setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).create().show();
            }
        });

        call_user.setOnClickListener(view -> {
            if (userPhoneNum != null && !userPhoneNum.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + userPhoneNum));
                startActivity(intent);
            } else {
                Toast.makeText(WorkDetailsActivity.this, "Can't make a call", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotificationForWorkAccepted() {
        String strJsonBody = "{"
                + "\"app_id\": \"" + OneSignal.oneSignalAppId + "\","
                + "\"filters\": [{\"field\": \"tag\", \"key\": \"" + UID + "\", \"relation\": \"=\", \"value\": \"" + work_posted_by_account_id + "\"},{\"operator\": \"OR\"},{\"field\": \"amount_spent\", \"relation\": \">\",\"value\": \"0\"}],"
                + "\"data\": {\"foo\": \"bar\"},"
                + "\"contents\": {\"en\": \"Your work has been accepted by " + currentUser.getDisplayName() + "\"}"
                + "}";
        OneSignal.sendNotification(strJsonBody);
    }

    private void sendNotificationForWorkCancelled() {
        String strJsonBody = "{"
                + "\"app_id\": \"" + OneSignal.oneSignalAppId + "\","
                + "\"filters\": [{\"field\": \"tag\", \"key\": \"" + UID + "\", \"relation\": \"=\", \"value\": \"" + work_posted_by_account_id + "\"},{\"operator\": \"OR\"},{\"field\": \"amount_spent\", \"relation\": \">\",\"value\": \"0\"}],"
                + "\"data\": {\"foo\": \"bar\"},"
                + "\"contents\": {\"en\": \"Your work has been cancelled by " + currentUser.getDisplayName() + "\"}"
                + "}";
        OneSignal.sendNotification(strJsonBody);
    }
}
