package soa.work.scheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import soa.work.scheduler.utilities.AppStatus;
import soa.work.scheduler.utilities.PrefManager;
import soa.work.scheduler.models.UserAccount;
import soa.work.scheduler.userAccount.MainActivity;
import soa.work.scheduler.workerAccount.WorkersActivity;

import static soa.work.scheduler.data.Constants.USER_ACCOUNT;
import static soa.work.scheduler.data.Constants.USER_ACCOUNTS;
import static soa.work.scheduler.userAccount.MainActivity.IS_ACCOUNT_DELETED;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 234;
    private AppStatus appStatus;
    //And also a Firebase Auth object
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private PrefManager prefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefManager = new PrefManager(this);

        mAuth = FirebaseAuth.getInstance();
        appStatus = new AppStatus(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(view -> {
            if (appStatus.isOnline()) {
                signIn();
            } else {
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Please Wait");
        pd.setCancelable(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            if (appStatus.isOnline()) {
                boolean fromMainActivity = getIntent().getBooleanExtra(IS_ACCOUNT_DELETED, false);
                if (!fromMainActivity) {
                    Intent intent;
                    if (prefManager.getLastOpenedActivity() == USER_ACCOUNT) {
                        intent = new Intent(this, MainActivity.class);
                    } else {
                        intent = new Intent(this, WorkersActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            } else {
                pd.dismiss();
                Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                pd.dismiss();
                //Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            OneSignal.sendTag("uid", user.getUid());
                        }
                        FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
                        DatabaseReference userAccountsRef = databaseRef.getReference(USER_ACCOUNTS);
                        userAccountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (user != null) {
                                    if (!dataSnapshot.hasChild(user.getUid())) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                                        String currentDateAndTime = sdf.format(new Date());
                                        UserAccount userAccount = new UserAccount();
                                        userAccount.setAccountCreatedOn(currentDateAndTime);
                                        userAccount.setEmail(user.getEmail());
                                        userAccount.setName(user.getDisplayName());
                                        userAccount.setWorkCategory("false");
                                        userAccountsRef.child(user.getUid()).setValue(userAccount);
                                        Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                                        Intent intent;
                                        if (prefManager.getLastOpenedActivity() == USER_ACCOUNT) {
                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                        } else {
                                            intent = new Intent(LoginActivity.this, WorkersActivity.class);
                                        }
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();
                                        Intent intent;
                                        if (prefManager.getLastOpenedActivity() == USER_ACCOUNT) {
                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                        } else {
                                            intent = new Intent(LoginActivity.this, WorkersActivity.class);
                                        }
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(LoginActivity.this, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });

                    } else {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void signIn() {
        pd.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
