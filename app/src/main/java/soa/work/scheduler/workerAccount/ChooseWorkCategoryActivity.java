package soa.work.scheduler.workerAccount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import soa.work.scheduler.CategoryRecyclerViewAdapter;
import soa.work.scheduler.R;
import soa.work.scheduler.models.Category;
import soa.work.scheduler.userAccount.MainActivity;

import static soa.work.scheduler.data.Constants.AC_REPAIRING;
import static soa.work.scheduler.data.Constants.CARPENTER;
import static soa.work.scheduler.data.Constants.CAR_MECHANIC;
import static soa.work.scheduler.data.Constants.CATERING;
import static soa.work.scheduler.data.Constants.ELECTRICIAN;
import static soa.work.scheduler.data.Constants.BIKE_MECHANIC;
import static soa.work.scheduler.data.Constants.HOME_TUTOR;
import static soa.work.scheduler.data.Constants.LAPTOP_OR_PC_REPAIRING;
import static soa.work.scheduler.data.Constants.MARVEL;
import static soa.work.scheduler.data.Constants.MECHANIC;
import static soa.work.scheduler.data.Constants.MOBILE_REPAIRING;
import static soa.work.scheduler.data.Constants.PAINTER;
import static soa.work.scheduler.data.Constants.PHONE_NUMBER;
import static soa.work.scheduler.data.Constants.PHOTOGRAPHY;
import static soa.work.scheduler.data.Constants.PLUMBER;
import static soa.work.scheduler.data.Constants.REFRIGERATOR_REPAIRING;
import static soa.work.scheduler.data.Constants.RENOVATION;
import static soa.work.scheduler.data.Constants.STUDENT_PROJECT;
import static soa.work.scheduler.data.Constants.T_SHIRT;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.data.Constants.VOLUNTEER;
import static soa.work.scheduler.data.Constants.WASHING_MACHINE_REPAIRING;
import static soa.work.scheduler.data.Constants.WEDDING;
import static soa.work.scheduler.data.Constants.WORK_CATEGORY;

public class ChooseWorkCategoryActivity extends AppCompatActivity  {

    @BindView(R.id.categories_recycler_view)
    RecyclerView categoriesRecyclerView;

    private ArrayList<Category> categories = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_work_category);

        ButterKnife.bind(this);

        categories.add(new Category(CARPENTER, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/ac_repair.jpg?alt=media&token=e0c02fc1-6027-4836-999f-4ce39bd31b98"));
        categories.add(new Category(MECHANIC,""));
        categories.add(new Category(AC_REPAIRING,"https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/ac_repair.jpg?alt=media&token=e0c02fc1-6027-4836-999f-4ce39bd31b98"));
        categories.add(new Category(BIKE_MECHANIC,"https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/bike_mechanic.jpg?alt=media&token=5af4bd28-83b2-41d3-8124-a6128ddcafc1"));
        categories.add(new Category(CAR_MECHANIC,"https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/car_mechanic.jpg?alt=media&token=a387c52b-28ff-48e4-8631-c0b438affd82"));
        categories.add(new Category(ELECTRICIAN, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/commercial_electrician.jpg?alt=media&token=fa383ae8-2589-4fd7-b14a-63049855e923"));
        categories.add(new Category(LAPTOP_OR_PC_REPAIRING, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/laptop_pc_repairing.jpg?alt=media&token=1f506625-29fe-44c0-96b7-19a08cac1e77"));
        categories.add(new Category(MOBILE_REPAIRING, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/mobile_repairing.jpg?alt=media&token=b867145e-e50e-492c-ac32-0c50f77dc268"));
        categories.add(new Category(PLUMBER, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/plumber.jpg?alt=media&token=a5c4e1c4-5b9d-4bfe-b5a7-6bc937a2b3c2"));
        categories.add(new Category(REFRIGERATOR_REPAIRING,"https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/refrigerator_repairing.jpg?alt=media&token=c020bd19-48fa-4828-be39-6fe6cb43a860"));
        categories.add(new Category(WASHING_MACHINE_REPAIRING, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/washing_machine.jpg?alt=media&token=a9fc6879-5e15-48c5-ba36-ede7eca87a47"));
        categories.add(new Category(PAINTER,""));
        categories.add(new Category(MARVEL, ""));
        categories.add(new Category(PHOTOGRAPHY, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/photography_videography.jpg?alt=media&token=a603c45f-699e-4595-ae76-7a56b3fe5e23"));
        //Softwatare solution price
        categories.add(new Category(CATERING, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/catering.jpg?alt=media&token=2877ead9-b7ed-4871-b9c1-fe7a27913afd"));
        categories.add(new Category(T_SHIRT, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/t_shirt.jpg?alt=media&token=985d7f68-cc34-4c43-a8e2-12001a1d80b2"));
        categories.add(new Category(WEDDING, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/wedding.jpg?alt=media&token=1687d7fc-ad5a-47c4-a1ba-96e3a3a3bb7d"));
        categories.add(new Category(STUDENT_PROJECT, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/project-Copy.jpg?alt=media&token=7e521ab2-cde9-4861-b84a-2cfded63bbd6"));
        categories.add(new Category(VOLUNTEER, ""));
        categories.add(new Category(HOME_TUTOR, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/home_tutor.jpg?alt=media&token=6942c6c2-15ff-43e2-b53b-ff739aa0ddf4"));
        categories.add(new Category(RENOVATION, "https://firebasestorage.googleapis.com/v0/b/work-scheduler-fc725.appspot.com/o/renovation_service.jpg?alt=media&token=bb328259-de94-4cce-a686-049c36dd7248"));


        CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(categories, this);
        categoryRecyclerViewAdapter.setItemClickListener(this::askPhoneNumber);
        categoriesRecyclerView.setAdapter(categoryRecyclerViewAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        categoriesRecyclerView.setLayoutManager(manager);
    }

    private void askPhoneNumber(Category category) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Contact Info is required");
        alertDialog.setMessage("Enter Phone number");

        final EditText input = new EditText(this);
        input.setHint("Phone number");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("DONE",
                (dialog, which) -> {
                    String phoneNumber = input.getText().toString();
                    if (phoneNumber.length() != 10) {
                        Toast.makeText(ChooseWorkCategoryActivity.this, "Phone number is not valid", Toast.LENGTH_SHORT).show();
                        askPhoneNumber(category);
                    } else {
                        FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
                        DatabaseReference currentUserAccount = databaseRef.getReference(USER_ACCOUNTS).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        currentUserAccount.child(WORK_CATEGORY).setValue(category.getCategoryTitle());
                        currentUserAccount.child(PHONE_NUMBER).setValue(phoneNumber);
                        OneSignal.sendTag(WORK_CATEGORY, category.getCategoryTitle());
                        startActivity(new Intent(ChooseWorkCategoryActivity.this, WorkersActivity.class));
                        finish();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChooseWorkCategoryActivity.this, MainActivity.class));
    }
}


