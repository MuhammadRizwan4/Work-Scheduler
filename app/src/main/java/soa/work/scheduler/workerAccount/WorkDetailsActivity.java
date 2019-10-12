package soa.work.scheduler.workerAccount;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soa.work.scheduler.R;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.RetrofitClient;
import soa.work.scheduler.models.OneSignalIds;
import soa.work.scheduler.models.UniversalWork;
import soa.work.scheduler.models.UserAccount;

import static soa.work.scheduler.data.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.data.Constants.UID;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKER_PHONE_NUMBER;
import static soa.work.scheduler.data.Constants.WORKS_POSTED;
import static soa.work.scheduler.data.Constants.WORK_ASSIGNED_AT;
import static soa.work.scheduler.data.Constants.WORK_ASSIGNED_TO;
import static soa.work.scheduler.data.Constants.WORK_ASSIGNED_TO_ID;
import static soa.work.scheduler.data.Constants.WORK_CANCEL;

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

    private FirebaseUser currentUser;
    private String created_date, work_posted_by_account_id;
    private String oneSignalAppId;
    private String oneSignalRestApiKey;
    private boolean is_assignned;
    private String account_id;
    private String created_Date;

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
        currentWork.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UniversalWork work = dataSnapshot.getValue(UniversalWork.class);
                postedAtTextView.setText(Objects.requireNonNull(work).getCreated_date());
                postedByTextView.setText(work.getWork_posted_by_name());
                priceRangeTextView.setText(String.format("Rs.%s - Rs.%s", work.getPrice_range_from(), work.getPrice_range_to()));
                userPhoneNumberTextView.setText(work.getUser_phone());
                userLocationTextView.setText(work.getWork_address());
                deadlineTextView.setText(work.getWork_deadline());
                account_id = work.getWork_posted_by_account_id();
                created_Date = work.getCreated_date();
                workDescriptionTextView.setText(work.getWork_description());
                if (work.getAssigned_to_id().equals(currentUser.getUid())) {
                    acceptWorkButton.setText("CANCEL WORK");
                    acceptWorkButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                    is_assignned = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        acceptWorkButton.setOnClickListener(view -> {
            if (!is_assignned) {
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
                                    currentWork.child(WORKER_PHONE_NUMBER).setValue(Objects.requireNonNull(userAccount).getPhone_number());

                                    DatabaseReference accountOfUser = database.getReference(USER_ACCOUNTS).child(work_posted_by_account_id);
                                    DatabaseReference workInUserHistory = accountOfUser.child(WORKS_POSTED).child(work_posted_by_account_id + "-" + created_date);
                                    workInUserHistory.child(WORK_ASSIGNED_TO).setValue(currentUser.getDisplayName());
                                    workInUserHistory.child(WORK_ASSIGNED_AT).setValue(currentDateAndTime);
                                    workInUserHistory.child(WORK_ASSIGNED_TO_ID).setValue(currentUser.getUid());
                                    workInUserHistory.child(WORKER_PHONE_NUMBER).setValue(userAccount.getPhone_number());
                                    acceptWorkButton.setText("CANCEL WORK");
                                    acceptWorkButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color));
                                    is_assignned = true;
                                    getOneSignalKeys();
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
                            DatabaseReference cancel_work = database.getReference(USER_ACCOUNTS).child(account_id).child(WORKS_POSTED)
                                    .child(account_id +"-"+ created_Date).child(WORK_CANCEL);
                            cancel_work.setValue(true);
                            /**
                             * FIXME Send notification here
                             */
                            currentWork.removeValue();
                            Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();

                        })
                        .setNegativeButton("NO", (dialog, which) -> {
                            dialog.dismiss();
                        }).create().show();
            }
        });
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
                        sendNotification();
                        AsyncTask.execute(() -> sendNotification());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OneSignalIds> call, @NonNull Throwable t) {

                }
            });
        }).addOnFailureListener(e -> {

        });
    }

    private void sendNotification() {
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic " + oneSignalRestApiKey);
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    + "\"app_id\": \"" + oneSignalAppId + "\","
                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"" + UID + "\", \"relation\": \"=\", \"value\": \"" + work_posted_by_account_id + "\"},{\"operator\": \"OR\"},{\"field\": \"amount_spent\", \"relation\": \">\",\"value\": \"0\"}],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"contents\": {\"en\": \"Your work has been accepted by " + currentUser.getDisplayName() + "\"}"
                    + "}";

            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes(StandardCharsets.UTF_8);
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            } else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
