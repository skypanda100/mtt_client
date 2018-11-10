package gzt.mtt.View.FoodGrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import gzt.mtt.Adapter.FoodGradeAdapter;
import gzt.mtt.BaseActivity;
import gzt.mtt.R;
public class FoodGradeActivity extends BaseActivity {
    private boolean mCanDelete;
    private int mIndex;
    private List<String> mImages;
    private FoodGradeAdapter mFoodGradeAdapter;
    private ViewPager mFoodViewPager;
    private TextView mFoodIndicatorTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.mCanDelete) {
            getMenuInflater().inflate(R.menu.food_grade, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            Intent intent = new Intent();
            intent.putExtra("index", this.mIndex);
            this.setResult(RESULT_OK, intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initData() {
        Intent intent = this.getIntent();
        this.mIndex = intent.getIntExtra("index", 0);
        this.mImages = intent.getStringArrayListExtra("images");
        this.mCanDelete = intent.getBooleanExtra("canDelete", false);
        this.mFoodGradeAdapter = new FoodGradeAdapter(this);
        this.mFoodGradeAdapter.setImages(this.mImages);
    }

    private void initView() {
        this.createFullScreenView();
        setContentView(R.layout.activity_food_grade);

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        this.mFoodIndicatorTextView = this.findViewById(R.id.foodIndicator);
        this.setIndicator();
        this.mFoodViewPager = this.findViewById(R.id.foodViewPager);
        this.mFoodViewPager.setAdapter(this.mFoodGradeAdapter);
        this.mFoodViewPager.setCurrentItem(this.mIndex);
        this.mFoodViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mIndex = i;
                setIndicator();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setIndicator() {
        this.mFoodIndicatorTextView.setText((this.mIndex + 1) + "/" + this.mImages.size());
    }
}
