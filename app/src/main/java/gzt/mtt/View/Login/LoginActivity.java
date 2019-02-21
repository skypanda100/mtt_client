package gzt.mtt.View.Login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.design.button.MaterialButton;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import gzt.mtt.View.BaseActivity;
import gzt.mtt.View.MainActivity;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import gzt.mtt.Util.CryptUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    private MaterialButton mLoginMaterialButton;
    private ProgressBar mWaitingProgressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initData();
        this.initView();
    }

    private void initData() {
    }

    private void initView() {
        setContentView(R.layout.activity_login);

        this.mUserNameEditText = this.findViewById(R.id.userName);
        this.mPasswordEditText = this.findViewById(R.id.password);
        this.mLoginMaterialButton = this.findViewById(R.id.login);
        this.mWaitingProgressbar = this.findViewById(R.id.waiting);

        if(this.mStorageManager.contain("userName")) {
            this.mUserNameEditText.setText((String)this.mStorageManager.getSharedPreference("userName", ""));
        }
        if(this.mStorageManager.contain("password")) {
            this.mPasswordEditText.setText((String)this.mStorageManager.getSharedPreference("password", ""));
        }
    }

    private void startWaitingAnimation() {
        final String loginText = (String) this.mLoginMaterialButton.getText();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this.mLoginMaterialButton, "scaleX", 1, 0.1f);
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLoginMaterialButton.setText("");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginMaterialButton.setVisibility(View.GONE);
                mLoginMaterialButton.setText(loginText);
                mWaitingProgressbar.setVisibility(View.VISIBLE);
                login();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    private void stopWaitingAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this.mLoginMaterialButton, "scaleX", 0.1f, 1);
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mWaitingProgressbar.setVisibility(View.GONE);
                mLoginMaterialButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    private void onLoginSuccess(String bodyStr) {
        try {
            String userName = this.mUserNameEditText.getText().toString();
            String password = this.mPasswordEditText.getText().toString();

            JSONObject jsonObject = new JSONObject(bodyStr);
            this.mStorageManager.put("alias", jsonObject.getString("alias"));
            this.mStorageManager.put("avatar", jsonObject.getString("avatar"));
            this.mStorageManager.put("token", jsonObject.getString("token"));
            this.mStorageManager.put("userName", userName);
            this.mStorageManager.put("password", password);

            this.startMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
            onLoginFailed("some errors happened in client");
        }
    }

    private void onLoginFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        this.stopWaitingAnimation();
    }

    private void login() {
        String userName = this.mUserNameEditText.getText().toString();
        String password = this.mPasswordEditText.getText().toString();
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

    public void onLoginClicked(View v) {
        this.startWaitingAnimation();
    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.class);
                finish();
            }
        }, 1000);
    }
}
