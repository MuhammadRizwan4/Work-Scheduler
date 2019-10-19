package soa.work.scheduler.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import soa.work.scheduler.models.OneSignalIds;
import soa.work.scheduler.retrofit.Weather.WeatherModel;

public interface ApiService {

    @GET
    Call<OneSignalIds> getOneSignalIds(@Url String url);

    @GET()
    Call<ResponseBody> getImage(@Url String url);

    @GET
    Call<WeatherModel> getWeatherData(@Url String url);
}
