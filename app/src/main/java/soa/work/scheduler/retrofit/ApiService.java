package soa.work.scheduler.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import soa.work.scheduler.models.OneSignalIds;

public interface ApiService {

    @GET
    Call<OneSignalIds> getOneSignalIds(@Url String url);

    @GET()
    Call<ResponseBody> getImage(@Url String url);

}
