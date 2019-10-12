package soa.work.scheduler.userAccount;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soa.work.scheduler.R;
import soa.work.scheduler.models.IndividualWork;
import soa.work.scheduler.models.OneSignalIds;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.RetrofitClient;
import soa.work.scheduler.utilities.OneSignal;

import static soa.work.scheduler.data.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.data.Constants.IS_WORK_AVAILABLE;
import static soa.work.scheduler.data.Constants.PHONE_NUMBER;
import static soa.work.scheduler.data.Constants.UID;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKS_POSTED;

public class WorkDetailsActivityForUser extends AppCompatActivity {

    @BindView(R.id.worker_name_text_view)
    EditText workerNameTextView;
    @BindView(R.id.worker_phone_number)
    EditText workerPhoneNumberTextView;
    @BindView(R.id.posted_at_text_view)
    EditText postedAtTextView;
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
    @BindView(R.id.phone_icon)
    ImageView call_worker;
    @BindView(R.id.mark_as_completed_button)
    Button markAsCompletedButton;
    @BindView(R.id.un_publish_button)
    Button unPublishButton;
    @BindView(R.id.worker_details_layout)
    LinearLayout workerDetailsLayout;

    private String created_date;
    private String assigned_to_id;
    private String workerPhoneNumber;
    private boolean isWorkAssigned = false;
    private String oneSignalAppId;
    private String oneSignalRestApiKey;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_details_for_user);

        ButterKnife.bind(this);

        workerNameTextView.setFocusable(false);
        workerPhoneNumberTextView.setFocusable(false);
        postedAtTextView.setFocusable(false);
        priceRangeTextView.setFocusable(false);
        userPhoneNumberTextView.setFocusable(false);
        userLocationTextView.setFocusable(false);
        deadlineTextView.setFocusable(false);
        workDescriptionTextView.setFocusable(false);

        setTitle("Details");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            created_date = (String) bundle.get("created_date");
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference currentWorkInHistory = database.getReference(USER_ACCOUNTS).child(firebaseUser.getUid())
                .child(WORKS_POSTED).child(firebaseUser.getUid() +
                        "-" + created_date);
        DatabaseReference currentWorkInCurrentlyAvailableWorks = database.getReference(CURRENTLY_AVAILABLE_WORKS).child(firebaseUser.getUid() + "-" + created_date);

        unPublishButton.setOnClickListener(view -> new AlertDialog.Builder(WorkDetailsActivityForUser.this)
                .setMessage("Are you sure want to un-publish this work?")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    currentWorkInHistory.child(IS_WORK_AVAILABLE).setValue(false);
                    currentWorkInCurrentlyAvailableWorks.removeValue();
                    markAsCompletedButton.setVisibility(View.GONE);
                    if (isWorkAssigned) {
                        getOneSignalKeys();
                    }
                    unPublishButton.setEnabled(false);
                    unPublishButton.setText("Un-Published");
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show());

        currentWorkInHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                IndividualWork work = dataSnapshot.getValue(IndividualWork.class);
                if (work != null) {
                    assigned_to_id = work.getAssigned_to_id();
                    postedAtTextView.setText(work.getCreated_date());
                    priceRangeTextView.setText("Rs." + work.getPrice_range_from() + " - Rs." + work.getPrice_range_to());
                    userPhoneNumberTextView.setText(work.getUser_phone());
                    userLocationTextView.setText(work.getWork_address());
                    deadlineTextView.setText(work.getWork_deadline());
                    workDescriptionTextView.setText(work.getWork_description());
                    workerPhoneNumberTextView.setText(" ");
                    if (work.getIs_work_available()) {
                        if (!work.getAssigned_to().isEmpty()) {
                            markAsCompletedButton.setVisibility(View.VISIBLE);
                        } else {
                            markAsCompletedButton.setVisibility(View.GONE);
                        }
                        boolean isWorkCompleted = work.getWork_completed();
                        if (isWorkCompleted) {
                            markAsCompletedButton.setText("Work Completed");
                            markAsCompletedButton.setEnabled(false);
                            unPublishButton.setVisibility(View.GONE);
                        } else {

                            markAsCompletedButton.setOnClickListener(v -> new AlertDialog.Builder(WorkDetailsActivityForUser.this)
                                    .setMessage("Are you sure want to mark work as Completed?")
                                    .setCancelable(false)
                                    .setPositiveButton("YES", (dialog, which) -> {
                                        Toast.makeText(WorkDetailsActivityForUser.this, "Marked as completed", Toast.LENGTH_SHORT).show();
                                        markAsCompletedButton.setText("Work Completed");
                                        markAsCompletedButton.setEnabled(false);
                                        currentWorkInCurrentlyAvailableWorks.child("work_completed").setValue(true);
                                        currentWorkInHistory.child("work_completed").setValue(true);
                                        unPublishButton.setVisibility(View.GONE);
                                    })
                                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                                    .create().show());
                        }
                    } else {
                        unPublishButton.setVisibility(View.VISIBLE);
                        unPublishButton.setEnabled(false);
                        unPublishButton.setText("Un-Published");
                        markAsCompletedButton.setVisibility(View.GONE);
                    }

                    if (!work.getAssigned_to().isEmpty()) {
                        isWorkAssigned = true;
                        workerNameTextView.setText(work.getAssigned_to());
                        workerDetailsLayout.setVisibility(View.VISIBLE);
                    } else {
                        isWorkAssigned = false;
                        workerNameTextView.setText(" ");
                        workerDetailsLayout.setVisibility(View.GONE);
                    }
                }
                DatabaseReference currentWorkerPhone = database.getReference().child(USER_ACCOUNTS).child(assigned_to_id).child(PHONE_NUMBER);
                currentWorkerPhone.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        workerPhoneNumber = dataSnapshot.getValue(String.class);
                        if (workerPhoneNumber != null && !workerPhoneNumber.isEmpty()) {
                            workerPhoneNumberTextView.setText(workerPhoneNumber);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        call_worker.setOnClickListener(view -> {
            if (workerPhoneNumber != null && !workerPhoneNumber.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + workerPhoneNumber));
                startActivity(intent);
            } else {
                Toast.makeText(WorkDetailsActivityForUser.this, "Can't make a call", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                                "{\"field\": \"tag\", \"key\": \"" + UID + "\", \"relation\": \"=\", \"value\": \"" + assigned_to_id + "\"}]," +
                                "\"contents\": {\"en\": \"" + firebaseUser.getDisplayName() + " has unpublished a work which was accepted by you.\"}" +
                                "}";
                        AsyncTask.execute(() -> OneSignal.sendNotification(oneSignalRestApiKey, jsonBody));
                        Toast.makeText(WorkDetailsActivityForUser.this, "Unpublished", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OneSignalIds> call, @NonNull Throwable t) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

}
