package gzt.mtt.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gzt.mtt.Adapter.FoodGradesAdapter;
import gzt.mtt.BaseActivity;
import gzt.mtt.Constant;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import gzt.mtt.View.AirQuality.AirQualityActivity;
import gzt.mtt.View.FoodGrade.FoodGradeActivity;
import gzt.mtt.View.FoodGrade.FoodGradesActivity;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_PERMISSION = 0;
    private static boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private MaterialRatingBar mAirQualityMaterialRatingBar;
    private RecyclerView mFoodGradesRecyclerView;
    private FoodGradesAdapter mFoodGradesAdapter;
    private JSONArray mFoodGrades;
    private JSONArray mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initData();
        this.initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_air_quality) {
            this.startActivity(AirQualityActivity.class);
        } else if (id == R.id.nav_sleep_quality) {

        } else if (id == R.id.nav_food_grade) {
            this.startActivity(FoodGradesActivity.class);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("zdt", permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("zdt", permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!this.isExit) {
            this.isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            this.mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    private void initData() {
    }

    private void initView() {
        this.setContentView(R.layout.activity_main);
        this.setPermissions();

        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 设置头像名称
        View headerView = navigationView.getHeaderView(0);

        AvatarImageView avatarImageView = headerView.findViewById(R.id.avatar);
        Picasso.with(this).load(Constant.BaseImageUrl + this.mStorageManager.getSharedPreference("avatar", "")).into(avatarImageView);

        TextView aliasTextView = headerView.findViewById(R.id.alias);
        aliasTextView.setText((String)this.mStorageManager.getSharedPreference("alias", ""));

        // 空气质量
        MaterialCardView airQualityContainer = this.findViewById(R.id.airQualityContainer);
        airQualityContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AirQualityActivity.class);
            }
        });
        this.mAirQualityMaterialRatingBar = this.findViewById(R.id.airQuality);
        this.showAirQuality();

        // 食物评分
        this.mFoodGradesRecyclerView = this.findViewById(R.id.foodGrades);
        this.setLayoutManagerPolicy(1);
        this.showFoodGrade();
    }

    private void setPermissions () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            }

            if (!permissions.isEmpty()) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSION);
            }
        }
    }
    
    private void switchFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction().replace(R.id.contentMain, fragment).commit();
    }

    private void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_back);
    }

    /** start:空气质量 **/
    private void showAirQuality() {
        Call<ResponseBody> call = HttpManager.instance().get("serials/last");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject resJson = new JSONObject(response.body().string());
                    JSONObject grade = AirQualityActivity.evaluateAirQuality(resJson);
                    float tempGrade = (float)grade.getDouble("tempGrade");
                    float humidityGrade = (float)grade.getDouble("humidityGrade");
                    float pm25Grade = (float)grade.getDouble("pm25Grade");
                    float co2Grade = (float)grade.getDouble("co2Grade");
                    float hchoGrade = (float)grade.getDouble("hchoGrade");
                    float avgGrade = (tempGrade + humidityGrade + pm25Grade + co2Grade + hchoGrade) / 5;
                    mAirQualityMaterialRatingBar.setRating(avgGrade);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    /** end:空气质量 **/
    /** start:食物评分 **/
    private void setLayoutManagerPolicy(int cols) {
        this.mFoodGradesRecyclerView.setLayoutManager(new GridLayoutManager(this, cols));
        this.mFoodGradesRecyclerView.setAdapter(mFoodGradesAdapter = new FoodGradesAdapter(this, cols == 1));
        this.mFoodGradesAdapter.setFoodGrades(this.mFoodGrades);
        this.mFoodGradesAdapter.setItemClickListener(new FoodGradesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    JSONObject foodGrade = mFoodGrades.getJSONObject(position);
                    ArrayList<String> images = new ArrayList<>();
                    images.add(foodGrade.getString("imagePath"));
                    if(foodGrade.has("others")){
                        JSONArray others = foodGrade.getJSONArray("others");
                        for(int i = 0;i < others.length();i++) {
                            images.add(others.getJSONObject(i).getString("imagePath"));
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, FoodGradeActivity.class);
                    intent.putExtra("alias", foodGrade.getString("alias"));
                    intent.putExtra("avatar", foodGrade.getString("avatar"));
                    intent.putExtra("dateTime", foodGrade.getString("dateTime"));
                    intent.putExtra("comment", foodGrade.getString("comment"));
                    intent.putExtra("grade", foodGrade.getInt("grade"));
                    intent.putStringArrayListExtra("images", images);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONObject getUser (String userName) {
        for(int i = 0;i < this.mUsers.length();i++) {
            try {
                JSONObject user = this.mUsers.getJSONObject(i);
                if(userName.compareTo(user.getString("username")) == 0) {
                    return user;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void onFetchAllUsersSuccess(JSONArray jsonArray) {
        this.mUsers = jsonArray;
    }

    private void onFetchAllUsersFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onFetchFoodGradesSuccess(JSONArray jsonArray) {
        try {
            for(int i = 0;i < jsonArray.length();i++) {
                JSONObject foodGrade = jsonArray.getJSONObject(i);
                JSONObject user = this.getUser(foodGrade.getString("user"));
                foodGrade.put("alias", user.getString("alias"));
                foodGrade.put("avatar", user.getString("avatar"));
            }
            this.mFoodGrades = jsonArray;
            if(this.mFoodGradesAdapter != null) {
                this.mFoodGradesAdapter.setFoodGrades(this.mFoodGrades);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onFetchFoodGradesFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showFoodGrade() {
        Call<ResponseBody> allUsersCall = HttpManager.instance().get("users/all");
        if(allUsersCall != null) {
            allUsersCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONArray resJsonArray = new JSONArray(response.body().string());
                        onFetchAllUsersSuccess(resJsonArray);

                        Map<String, String> options = new HashMap<>();
                        options.put("sort", "-dateTime");
                        Call<ResponseBody> foodGradesCall = HttpManager.instance().get("foodGrades", options);
                        if(foodGradesCall != null) {
                            foodGradesCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        JSONArray resJsonArray = new JSONArray(response.body().string());
                                        JSONArray newJsonArray = new JSONArray();
                                        newJsonArray.put(resJsonArray.getJSONObject(0));
                                        onFetchFoodGradesSuccess(newJsonArray);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onFetchFoodGradesFailed("some errors happened in server");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    onFetchFoodGradesFailed("some errors happened in server");
                                }
                            });
                        } else {
                            onFetchFoodGradesFailed("some errors happened in client");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFetchAllUsersFailed("some errors happened in server");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    onFetchAllUsersFailed("some errors happened in server");
                }
            });
        } else {
            onFetchAllUsersFailed("some errors happened in client");
        }
    }
    /** end:食物评分 **/
}
