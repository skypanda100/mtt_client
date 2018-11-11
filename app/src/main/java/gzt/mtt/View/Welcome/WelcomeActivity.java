package gzt.mtt.View.Welcome;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import gzt.mtt.BaseActivity;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.Manager.StorageManager;
import gzt.mtt.R;
import gzt.mtt.Util.CryptUtil;
import gzt.mtt.View.Login.LoginActivity;
import gzt.mtt.View.MainActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    private void initData() {
        this.mStorageManager = new StorageManager(this);

        String userName = "";
        String password = "";
        if(this.mStorageManager.contain("userName")) {
            userName = (String)this.mStorageManager.getSharedPreference("userName", "");
        }
        if(this.mStorageManager.contain("password")) {
            password = (String)this.mStorageManager.getSharedPreference("password", "");
        }
        if(userName.compareTo("") == 0 || password.compareTo("") == 0) {
            this.startLoginActivity();
        } else {
            String encryptPwd = CryptUtil.base64(CryptUtil.sha256(password));
            HashMap<String, Object> body = new HashMap<>();
            body.put("username", userName);
            body.put("password", encryptPwd);

            Call<ResponseBody> call = HttpManager.instance().post("users/token", body);
            if(call != null) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            onLoginSuccess(response.body().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                            onLoginFailed("some errors happened in server");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        onLoginFailed("some errors happened in server");
                    }
                });
            } else {
                onLoginFailed("some errors happened in client");
            }
        }
    }

    private void initView() {
        this.createImmerseView();
        this.setContentView(R.layout.activity_welcome);
    }

    private void onLoginSuccess(String bodyStr) {
        try {
            JSONObject jsonObject = new JSONObject(bodyStr);
            HttpManager.instance().setToken(jsonObject.getString("token"));

            this.mStorageManager.put("alias", jsonObject.getString("alias"));
            this.mStorageManager.put("avatar", jsonObject.getString("avatar"));
            this.mStorageManager.put("token", jsonObject.getString("token"));

            this.startMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
            onLoginFailed("some errors happened in client");
        }
    }

    private void onLoginFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        this.startLoginActivity();
    }

    private void startLoginActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(LoginActivity.class);
                finish();
            }
        }, 700);
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.class);
                finish();
            }
        }, 700);
    }
}
