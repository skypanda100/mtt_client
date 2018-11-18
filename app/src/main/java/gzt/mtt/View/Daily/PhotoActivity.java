package gzt.mtt.View.Daily;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import gzt.mtt.Adapter.PhotoAdapter;
import gzt.mtt.View.BaseActivity;
import gzt.mtt.R;
public class PhotoActivity extends BaseActivity {
    private boolean mCanDelete;
    private int mIndex;
    private List<String> mImages;
    private PhotoAdapter mPhotoAdapter;
    private ViewPager mPhotoViewPager;
    private TextView mPhotoIndicatorTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.mCanDelete) {
            getMenuInflater().inflate(R.menu.photo, menu);
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
        this.mPhotoAdapter = new PhotoAdapter(this);
        this.mPhotoAdapter.setImages(this.mImages);
    }

    private void initView() {
        this.createImmerseView();
        setContentView(R.layout.activity_photo);

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

        this.mPhotoIndicatorTextView = this.findViewById(R.id.photoIndicator);
        this.setIndicator();
        this.mPhotoViewPager = this.findViewById(R.id.photoViewPager);
        this.mPhotoViewPager.setAdapter(this.mPhotoAdapter);
        this.mPhotoViewPager.setCurrentItem(this.mIndex);
        this.mPhotoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        this.mPhotoIndicatorTextView.setText((this.mIndex + 1) + "/" + this.mImages.size());
    }
}
