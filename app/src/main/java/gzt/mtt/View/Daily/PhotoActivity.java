package gzt.mtt.View.Daily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.util.List;

import gzt.mtt.Adapter.PhotoAdapter;
import gzt.mtt.Manager.ImageManager;
import gzt.mtt.Util.PathUtil;
import gzt.mtt.Util.PhotoUtil;
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
        getMenuInflater().inflate(R.menu.photo, menu);
        if (!this.mCanDelete) {
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);
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
        } else if (id == R.id.action_share) {
            this.share();
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

    private void share() {
        String title = "MTT";
        String subject = "";
        String content = "";
        String imagePath = this.mImages.get(this.mIndex);
        URI uri = null;
        if (imagePath.startsWith("/storage")) {
            uri = new File(imagePath).toURI();
        } else {
//            ImageManager.loadImage(this.mContext, path, photoView);
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (content != null && !"".equals(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }

        // 设置弹出框标题
        if (title != null && !"".equals(title)) { // 自定义标题
            startActivity(Intent.createChooser(intent, title));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }
}
