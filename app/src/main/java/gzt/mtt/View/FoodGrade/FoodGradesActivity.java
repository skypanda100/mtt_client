package gzt.mtt.View.FoodGrade;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
    private RecyclerView.Adapter mFoodGradesAdapter;
    private JSONArray mFoodGrades;
    private JSONArray mUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initData() {
        this.fetchAllUsers();
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
        this.mFoodGradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        this.mFoodGradesRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        this.mFoodGradesRecyclerView.setAdapter(mFoodGradesAdapter = new FoodGradesAdapter());

        this.showFoodGrades();
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

    private void fetchAllUsers() {
        Call<ResponseBody> call = HttpManager.instance().get("users/all");
        if(call != null) {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONArray resJsonArray = new JSONArray(response.body().string());
                        onFetchAllUsersSuccess(resJsonArray);
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
            this.onFetchAllUsersFailed("some errors happened in client");
        }
    }

    private void onFetchAllUsersSuccess(JSONArray jsonArray) {
        this.mUsers = jsonArray;
    }

    private void onFetchAllUsersFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showFoodGrades() {
        Map<String, String> options = new HashMap<>();
        options.put("sort", "-dateTime");
        Call<ResponseBody> call = HttpManager.instance().get("foodGrades", options);
        if(call != null) {
            call.enqueue(new Callback<ResponseBody>() {
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
            this.onFetchFoodGradesFailed("some errors happened in client");
        }
    }

    private void onFetchFoodGradesSuccess(JSONArray jsonArray) {
        this.mFoodGrades = jsonArray;
        this.mFoodGradesAdapter.notifyDataSetChanged();
    }

    private void onFetchFoodGradesFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    class FoodGradesAdapter extends RecyclerView.Adapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            FoodGradesViewHolder foodGradesViewHolder = new FoodGradesViewHolder(LayoutInflater.from(FoodGradesActivity.this)
                    .inflate(R.layout.item_food_grades, viewGroup, false));
            return foodGradesViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            FoodGradesViewHolder foodGradesViewHolder = (FoodGradesViewHolder) viewHolder;
            try{
                JSONObject foodGrade = mFoodGrades.getJSONObject(i);

                String imagePath = foodGrade.getString("imagePath");
                String userName = foodGrade.getString("user");
                JSONObject user = getUser(userName);
                String alias = user.getString("alias");
                String avatar = user.getString("avatar");
                String dateTime = foodGrade.getString("dateTime");
                String comment = foodGrade.getString("comment");
                int grade = foodGrade.getInt("grade");

                Picasso.get().load(Constant.BaseImageUrl + avatar).into(foodGradesViewHolder.mAvatarAvatarImageView);
                foodGradesViewHolder.mAliasTextView.setText(alias);
                Picasso.get().load(Constant.BaseImageUrl + imagePath).into(foodGradesViewHolder.mFoodAppCompatImageView);
                foodGradesViewHolder.mGradeMaterialRatingBar.setRating(grade);
                foodGradesViewHolder.mDateTimeTextView.setText(dateTime);
                foodGradesViewHolder.mCommentTextView.setText(comment);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if(mFoodGrades != null) {
                return mFoodGrades.length();
            }
            return 0;
        }

        class FoodGradesViewHolder extends RecyclerView.ViewHolder {
            AvatarImageView mAvatarAvatarImageView;
            TextView mAliasTextView;
            AppCompatImageView mFoodAppCompatImageView;
            MaterialRatingBar mGradeMaterialRatingBar;
            TextView mDateTimeTextView;
            TextView mCommentTextView;
            public FoodGradesViewHolder(@NonNull View itemView) {
                super(itemView);
                this.mAvatarAvatarImageView = itemView.findViewById(R.id.avatar);
                this.mAliasTextView = itemView.findViewById(R.id.alias);
                this.mFoodAppCompatImageView = itemView.findViewById(R.id.food);
                this.mGradeMaterialRatingBar = itemView.findViewById(R.id.grade);
                this.mDateTimeTextView = itemView.findViewById(R.id.dateTime);
                this.mCommentTextView = itemView.findViewById(R.id.comment);
            }
        }
    }
}
