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
import java.util.regex.Pattern;

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

        categories.add(new Category(CARPENTER, "carpenter.jpg"));
        categories.add(new Category(MECHANIC,""));
        categories.add(new Category(AC_REPAIRING,"ac_repair.jpg"));
        categories.add(new Category(BIKE_MECHANIC,"bike_mechanic.jpg"));
        categories.add(new Category(CAR_MECHANIC,"car_mechanic.jpg"));
        categories.add(new Category(ELECTRICIAN, "commercial_electrician.jpg"));
        categories.add(new Category(LAPTOP_OR_PC_REPAIRING, "laptop_pc_repairing.jpg"));
        categories.add(new Category(MOBILE_REPAIRING, "mobile_repairing.jpg"));
        categories.add(new Category(PLUMBER, "plumber.jpg"));
        categories.add(new Category(REFRIGERATOR_REPAIRING,"refrigerator_repairing.jpg"));
        categories.add(new Category(WASHING_MACHINE_REPAIRING, "washing_machine.jpg"));
        categories.add(new Category(PAINTER,""));
        categories.add(new Category(MARVEL, ""));
        categories.add(new Category(PHOTOGRAPHY, "photography_videography.jpg"));
        //Softwatare solution price
        categories.add(new Category(CATERING, "catering.jpg"));
        categories.add(new Category(T_SHIRT, "t_shirt.jpg"));
        categories.add(new Category(WEDDING, "wedding.jpg"));
        categories.add(new Category(STUDENT_PROJECT, "project-Copy.jpg"));
        categories.add(new Category(VOLUNTEER, ""));
        categories.add(new Category(HOME_TUTOR, "home_tutor.jpg"));
        categories.add(new Category(RENOVATION, "renovation_service.jpg"));


        CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(categories, this);
        categoryRecyclerViewAdapter.setItemClickListener(this::askPhoneNumber);
        categoriesRecyclerView.setAdapter(categoryRecyclerViewAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        categoriesRecyclerView.setLayoutManager(manager);
    }
    
    /**
     * validates phoneNo 
     * @param phoneNo
     * @return
     */
    private boolean isInvalid(String phoneNo) {
		// validate phone numbers of format "1234567890"
		if (phoneNo.matches("\\d{10}"))
			return false;
		// validating phone number with -, . or spaces
		else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
			return false;
		// validating phone number with extension length from 3 to 5
		else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
			return false;
		// validating phone number where area code is in braces ()
		else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
			return false;
		// return false if nothing matches the input
		else return true;
		
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
                    
                    if (isInvalid(phoneNumber)) {
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


