package soa.work.scheduler.models;

import java.util.List;

import retrofit2.Callback;
import retrofit2.http.GET;

public interface api {
    @GET("api/RetrofitAndroidImageResponse")
    public void getData(Callback<List<Category>> response);
}
