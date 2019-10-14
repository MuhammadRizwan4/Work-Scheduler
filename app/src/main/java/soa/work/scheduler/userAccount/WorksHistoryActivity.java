package soa.work.scheduler.userAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.R;
import soa.work.scheduler.models.IndividualWork;

import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.WORKS_POSTED;

public class WorksHistoryActivity extends AppCompatActivity implements WorksHistoryAdapter.ItemCLickListener {

    @BindView(R.id.history_recycler_view)
    RecyclerView historyRecyclerView;
    @BindView(R.id.no_history)
    TextView noHistoryTextView;

    private WorksHistoryAdapter worksHistoryAdapter;
    private ArrayList<IndividualWork> workList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works_history);

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        progressDialog.show();

        setTitle("History");

        worksHistoryAdapter = new WorksHistoryAdapter(workList, this);
        worksHistoryAdapter.setItemClickListener(this);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setHasFixedSize(true);
        historyRecyclerView.setAdapter(worksHistoryAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userAccounts = database.getReference(USER_ACCOUNTS);
        DatabaseReference userAccount = userAccounts.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        DatabaseReference worksPosted = userAccount.child(WORKS_POSTED);

        worksPosted.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workList.clear();
                progressDialog.dismiss();
                if (dataSnapshot.getChildrenCount() == 0) {
                    noHistoryTextView.setVisibility(View.VISIBLE);
                    worksHistoryAdapter.notifyDataSetChanged();
                    return;
                }
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    workList.add(item.getValue(IndividualWork.class));
                }


                Collections.sort(workList, new Comparator<IndividualWork>() {
                    @Override
                    public int compare(IndividualWork individualWork, IndividualWork t1) {
                        return individualWork.getCreatedDate().compareTo(t1.getCreatedDate());
                    }
                });
                Collections.reverse(workList);
                noHistoryTextView.setVisibility(View.GONE);
                worksHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WorksHistoryActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(IndividualWork work) {
        Intent intent = new Intent(WorksHistoryActivity.this, WorkDetailsActivityForUser.class);
        intent.putExtra("created_date", work.getCreatedDate());
        startActivity(intent);
    }
}
