package soa.work.scheduler;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soa.work.scheduler.models.OneSignalIds;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.RetrofitClient;
import soa.work.scheduler.utilities.NotificationOpenedHandler;

public class Application extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .init();

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
                        soa.work.scheduler.utilities.OneSignal.oneSignalAppId = response.body().getOnesignalAppId();
                        soa.work.scheduler.utilities.OneSignal.oneSignalRestApiKey = response.body().getRestApiKey();
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
