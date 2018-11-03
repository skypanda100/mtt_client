package gzt.mtt.View.FoodGrade;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.List;

import gzt.mtt.Adapter.FoodGradeAdapter;
import gzt.mtt.R;
public class FoodGradeActivity extends AppCompatActivity {
    private String mAlias;
    private String mAvatar;
    private String mDateTime;
    private String mComment;
    private int mGrade;
    private List<String> mImages;
    private FoodGradeAdapter mFoodGradeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_food_grade);

        this.initData();
        this.initView();
    }

    private void initData() {
        Intent intent = this.getIntent();
        this.mAlias = intent.getStringExtra("alias");
        this.mAvatar = intent.getStringExtra("avatar");
        this.mDateTime = intent.getStringExtra("dateTime");
        this.mComment = intent.getStringExtra("comment");
        this.mGrade = intent.getIntExtra("grade", 0);
        this.mImages = intent.getStringArrayListExtra("images");

        this.mFoodGradeAdapter = new FoodGradeAdapter();
    }

    private void initView() {
        Banner foodGradeBanner = findViewById(R.id.foodGradeBanner);
        foodGradeBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        foodGradeBanner.setImages(this.mImages).setImageLoader(this.mFoodGradeAdapter).start();
    }
}
