package soa.work.scheduler.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import soa.work.scheduler.Reposistories.WeatherRepository_user;
import soa.work.scheduler.retrofit.Weather.WeatherModel;


public class WeatherViewModel_user extends AndroidViewModel {

    private WeatherRepository_user repository;

    public WeatherViewModel_user(@NonNull Application application) {
        super(application);
        repository = new WeatherRepository_user();
    }

    public LiveData<WeatherModel> getData() {
        return repository.getData();
    }

    public void refresh() {
        repository.refresh();
    }
}
