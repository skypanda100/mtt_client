package gzt.mtt.Manager;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public class HttpManager {
    private static HttpManager httpManager = null;
    private Retrofit mRetrofit;
    private RetrofitService mRetrofitService;
    private String mBaseUrl = "http://47.94.165.17:90/";
    private String mToken = "";

    public interface RetrofitService {
        @GET
        Call<ResponseBody> get(@Header("mtt-token")String token, @Url String url);

        @POST
        Call<ResponseBody> post(@Header("mtt-token")String token, @Url String url, @Body RequestBody requestBody);
    }

    private HttpManager() {
        this.mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .build();
        this.mRetrofitService = this.mRetrofit.create(RetrofitService.class);
    }

    public static synchronized HttpManager instance() {
        if(httpManager == null) {
            httpManager = new HttpManager();
        }
        return httpManager;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public Call<ResponseBody> get(String url) {
        return mRetrofitService.get(mToken, url);
    }

    public Call<ResponseBody> post(String url, HashMap<String, Object> paramMap) {
        try{
            JSONObject bodyJson = new JSONObject();
            Set<String> keySet = paramMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                Object value = paramMap.get(key);
                bodyJson.put(key, value);
            }
            String json = bodyJson.toString();
            Log.d("zdt", json);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            return mRetrofitService.post(mToken, url, requestBody);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
