package soa.work.scheduler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static soa.work.scheduler.Constants.CURRENTLY_AVAILABLE_WORKS;
import static soa.work.scheduler.Constants.PHONE_NUMBER;
import static soa.work.scheduler.Constants.USER_ACCOUNTS;

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

    private String created_date;
    private String assigned_to_id;
    private String workerPhoneNumber;

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

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            created_date = (String) bundle.get("created_date");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference currentWork = database.getReference(CURRENTLY_AVAILABLE_WORKS).child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "-" + created_date);
        currentWork.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UniversalWork work = dataSnapshot.getValue(UniversalWork.class);
                if (work != null) {
                    assigned_to_id = work.getAssigned_to_id();
                }
                if (work != null) {
                    postedAtTextView.setText(work.getCreated_date());
                }
                if (work != null) {
                    priceRangeTextView.setText("Rs." + work.getPrice_range_from() + " - Rs." + work.getPrice_range_to());
                }
                if (work != null) {
                    userPhoneNumberTextView.setText(work.getUser_phone());
                }
                if (work != null) {
                    userLocationTextView.setText(work.getWork_address());
                }
                deadlineTextView.setText(work.getWork_deadline());
                workDescriptionTextView.setText(work.getWork_description());
                workerPhoneNumberTextView.setText(" ");
                if (!work.getAssigned_to().isEmpty()) {
                    workerNameTextView.setText(work.getAssigned_to());
                } else {
                    workerNameTextView.setText(" ");
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

}
