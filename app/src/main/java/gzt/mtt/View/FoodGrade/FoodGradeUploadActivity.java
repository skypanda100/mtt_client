package gzt.mtt.View.FoodGrade;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.ArrayList;
import java.util.List;

import gzt.mtt.Adapter.FoodGradeAdapter;
import gzt.mtt.Adapter.FoodGradeUploadAdapter;
import gzt.mtt.Adapter.FoodGradesAdapter;
import gzt.mtt.R;
import gzt.mtt.Util.PathUtil;

public class FoodGradeUploadActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 0;
    private String mAlias;
    private String mAvatar;
    private String mDateTime;
    private String mComment;
    private int mGrade;
    private List<Object> mFoods = new ArrayList<>();
    private RecyclerView mFoodsRecyclerView;
    private FoodGradeUploadAdapter mFoodGradeUploadAdapter;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);
            this.initFoods();
            for (int i = 0;i < selected.size();i++) {
                this.mFoods.add(this.mFoods.size() - 1, selected.get(i));
            }
            this.mFoodGradeUploadAdapter.setFoods(this.mFoods);
        }
    }

    private void initFoods() {
        this.mFoods.clear();
        this.mFoods.add(R.drawable.plus);
    }

    private void initData() {
        this.initFoods();
    }

    private void initView() {
        this.setContentView(R.layout.activity_food_grade_upload);

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("提交评分");
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

        this.mFoodsRecyclerView = this.findViewById(R.id.foods);
        this.mFoodsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.mFoodsRecyclerView.setAdapter(mFoodGradeUploadAdapter = new FoodGradeUploadAdapter(this));
        this.mFoodGradeUploadAdapter.setItemClickListener(new FoodGradeUploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(FoodGradeUploadActivity.this, "" + position, Toast.LENGTH_LONG).show();
                if (mFoods.size() - 1 == position) {
                    openGallery();
                }
            }
        });
        this.mFoodGradeUploadAdapter.setFoods(this.mFoods);
    }

    private void openGallery () {
        Matisse.from(this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }
}
