package soa.work.scheduler.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit retrofit;
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://firebasestorage.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static Retrofit getRetrofitInstanceForImageDownload() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://drive.google.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }

    public static ApiService getApiServiceForImageDownload() {
        return getRetrofitInstanceForImageDownload().create(ApiService.class);
    }
}
