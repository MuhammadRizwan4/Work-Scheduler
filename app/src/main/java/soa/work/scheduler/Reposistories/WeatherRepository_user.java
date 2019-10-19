package soa.work.scheduler.Reposistories;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import soa.work.scheduler.BuildConfig;
import soa.work.scheduler.R;
import soa.work.scheduler.retrofit.ApiService;
import soa.work.scheduler.retrofit.Weather.WeatherClient;
import soa.work.scheduler.retrofit.Weather.WeatherModel;
import soa.work.scheduler.userAccount.MainActivity;

public class WeatherRepository_user {
    private static MutableLiveData<WeatherModel> data = new MutableLiveData<>();

    private ApiService apiService;
    private static Call<WeatherModel> call;

    public WeatherRepository_user(){
        apiService = WeatherClient.getApiService();
        loadData();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String weatherCity = sharedPreferences.getString(
                MainActivity.activity.getString(R.string.weather_city_key),
                MainActivity.activity.getString(R.string.weather_city_default)
        );

        boolean detectCityAutomatically = sharedPreferences.getBoolean(
                MainActivity.activity.getString(R.string.detect_city_automatically_key),
                Boolean.parseBoolean(MainActivity.activity.getString(R.string.detect_city_automatically_default))
        );
        String city;
        if (detectCityAutomatically) {
            city = MainActivity.city;
        } else {
            city = weatherCity;
        }

        String url = "weather?q=" + city + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        call = apiService.getWeatherData(url);
        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.e("WeatherRepository_user", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public void refresh(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.activity);
        String weatherCity = sharedPreferences.getString(
                MainActivity.activity.getString(R.string.weather_city_key),
                MainActivity.activity.getString(R.string.weather_city_default)
        );
        boolean detectCityAutomatically = sharedPreferences.getBoolean(
                MainActivity.activity.getString(R.string.detect_city_automatically_key),
                Boolean.parseBoolean(MainActivity.activity.getString(R.string.detect_city_automatically_default))
        );
        String city;
        if (detectCityAutomatically) {
            city = MainActivity.city;
        } else {
            city = weatherCity;
        }
        String url = "weather?q=" + city + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        Log.e("lala", "" + MainActivity.city);
        call = apiService.getWeatherData(url);
        call.clone().enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.e("WeatherRepository_user", "onFailure", t);
                data.postValue(null);
            }
        });
    }

    public LiveData<WeatherModel> getData() {
        return data;
    }
}
