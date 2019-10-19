package soa.work.scheduler.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import soa.work.scheduler.Reposistories.WeatherRepository_worker;
import soa.work.scheduler.retrofit.Weather.WeatherModel;


public class WeatherViewModel_worker extends AndroidViewModel {

    private WeatherRepository_worker repository;

    public WeatherViewModel_worker(@NonNull Application application) {
        super(application);
        repository = new WeatherRepository_worker();
    }

    public LiveData<WeatherModel> getData() {
        return repository.getData();
    }

    public void refresh() {
        repository.refresh();
    }
}
