package gzt.mtt.Manager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gzt.mtt.Constant;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public class HttpManager {
    private static HttpManager httpManager = null;
    private Retrofit mRetrofit;
    private RetrofitService mRetrofitService;
    private String mToken = "";

    public interface RetrofitService {
        @GET
        Call<ResponseBody> get(@Header("mtt-token")String token, @Url String url);

        @GET
        Call<ResponseBody> get(@Header("mtt-token")String token, @Url String url, @QueryMap Map<String, String> options);

        @POST
        Call<ResponseBody> post(@Header("mtt-token")String token, @Url String url, @Body RequestBody requestBody);

        @Multipart
        @PUT
        Call<ResponseBody> put(@Header("mtt-token")String token, @Url String url, @PartMap Map<String, RequestBody> params);
    }

    private HttpManager() {
        this.mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.BaseUrl)
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

    public Call<ResponseBody> get(String url, Map<String, String> options) {
        return mRetrofitService.get(mToken, url, options);
    }

    public Call<ResponseBody> post(String url, Map<String, Object> params) {
        try{
            JSONObject bodyJson = new JSONObject();
            Set<String> keySet = params.keySet();
            Iterator<String> iterator = keySet.iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                Object value = params.get(key);
                bodyJson.put(key, value);
            }
            String json = bodyJson.toString();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            return mRetrofitService.post(mToken, url, requestBody);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call<ResponseBody> put(String url, Map<String, Object> params) {
        Map<String, RequestBody> bodyParams = new HashMap<>();
        Set<String> keySet = params.keySet();
        Iterator<String> iterator = keySet.iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            Object value = params.get(key);
            if (value instanceof File) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), (File) value);
                bodyParams.put("file\";filename=\"" + key, requestFile);
            } else if (value instanceof JSONArray || value instanceof JSONObject) {
                bodyParams.put(key, RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(value)));
            } else {
                bodyParams.put(key, RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value)));
            }
        }
        return mRetrofitService.put(mToken, url, bodyParams);
    }
}
