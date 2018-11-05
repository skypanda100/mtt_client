package gzt.mtt.View.FoodGrade;

import android.content.Intent;
import android.os.Bundle;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import gzt.mtt.Adapter.FoodGradeAdapter;
import gzt.mtt.BaseActivity;
import gzt.mtt.R;
public class FoodGradeActivity extends BaseActivity {
    private String mAlias;
    private String mAvatar;
    private String mDateTime;
    private String mComment;
    private int mGrade;
    private List<String> mTitles;
    private List<String> mImages;
    private FoodGradeAdapter mFoodGradeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initData() {
        Intent intent = this.getIntent();
        this.mAlias = intent.getStringExtra("alias");
        this.mAvatar = intent.getStringExtra("avatar");
        this.mDateTime = intent.getStringExtra("dateTime");
        this.mComment = intent.getStringExtra("comment");
        this.mGrade = intent.getIntExtra("grade", 0);
        this.mImages = intent.getStringArrayListExtra("images");
        this.mTitles = new ArrayList<>();
        for(int i = 0;i < this.mImages.size();i++) {
            this.mTitles.add(this.mComment);
        }
        this.mFoodGradeAdapter = new FoodGradeAdapter();
    }

    private void initView() {
        this.createFullScreenView();
        setContentView(R.layout.activity_food_grade);

        Banner foodGradeBanner = findViewById(R.id.foodGradeBanner);
        foodGradeBanner.isAutoPlay(false);
        foodGradeBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        foodGradeBanner.setBannerTitles(this.mTitles);
        foodGradeBanner.setImages(this.mImages);
        foodGradeBanner.setImageLoader(this.mFoodGradeAdapter);
        foodGradeBanner.start();
    }
}
