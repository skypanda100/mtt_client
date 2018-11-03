package gzt.mtt.View.FoodGrade;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gzt.mtt.Adapter.FoodGradesAdapter;
import gzt.mtt.Constant;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodGradesActivity extends AppCompatActivity {

    private RecyclerView mFoodGradesRecyclerView;
    private FoodGradesAdapter mFoodGradesAdapter;
    private JSONArray mFoodGrades;
    private JSONArray mUsers;
    private boolean mIsOneCol = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_grades, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view) {
            if(this.mIsOneCol) {
                item.setIcon(R.drawable.ic_menu_one_col);
                this.setLayoutManagerPolicy(3);
            } else {
                item.setIcon(R.drawable.ic_menu_three_col);
                this.setLayoutManagerPolicy(1);
            }
            this.mIsOneCol = !this.mIsOneCol;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_forward, R.anim.slide_out_right);
    }

    private void initData() {
        this.fetchData();
    }

    private void initView() {
        this.setContentView(R.layout.activity_food_grades);

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("食物评分");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        this.mFoodGradesRecyclerView = this.findViewById(R.id.foodGrades);
        this.setLayoutManagerPolicy(1);
    }

    private void setLayoutManagerPolicy(int cols) {
        this.mFoodGradesRecyclerView.setLayoutManager(new GridLayoutManager(this, cols));
        this.mFoodGradesRecyclerView.setAdapter(mFoodGradesAdapter = new FoodGradesAdapter(this, cols == 1));
        this.mFoodGradesAdapter.setFoodGrades(this.mFoodGrades);
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

    private void fetchData() {
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
                                        onFetchFoodGradesSuccess(resJsonArray);
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
}
