package soa.work.scheduler.retrofit.Weather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import soa.work.scheduler.Supportclasses.DataHolder;
import soa.work.scheduler.retrofit.ApiService;

public class WeatherClient {
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(DataHolder.WEATHER_REQUEST_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
