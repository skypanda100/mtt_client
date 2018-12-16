package gzt.mtt.View.Daily;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import gzt.mtt.Adapter.PhotoAdapter;
import gzt.mtt.GlideApp;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.Manager.ImageManager;
import gzt.mtt.Util.PathUtil;
import gzt.mtt.Util.PhotoUtil;
import gzt.mtt.View.BaseActivity;
import gzt.mtt.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onResume() {
        super.onResume();
        this.createImmerseView();
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

        final Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
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
        toolbar.setVisibility(View.GONE);

        this.mPhotoAdapter.setImageClickListener(new PhotoAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(View view) {
                if (toolbar.getVisibility() == View.GONE) {
                    toolbar.setVisibility(View.VISIBLE);
                } else {
                    toolbar.setVisibility(View.GONE);
                }
            }
        });

        this.mPhotoAdapter.setImageLongClickListener(new PhotoAdapter.OnImageLongClickListener() {
            @Override
            public void onImageLongClick(View view) {
                Log.d("zdt", "long");
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

    private File writeResponseBodyToDisk(ResponseBody body) {
        try {
            File file = new File(getExternalFilesDir(null) + File.separator + body.hashCode());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                int read = -1;
                while ((read = inputStream.read(fileReader)) != -1) {
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("zdt", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return file;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    private void share(Uri uri) {
        String title = "MTT";
        String subject = "";
        String content = "";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(intent, title));
    }

    private void share() {
        final String imagePath = this.mImages.get(this.mIndex);
        if (imagePath.startsWith("/storage")) {
            File file = new File(imagePath);
            Uri uri = FileProvider.getUriForFile(PhotoActivity.this, "gzt.mtt.fileprovider", file);
            share(uri);
        } else {
            Call<ResponseBody> downloadCall = HttpManager.instance().get(imagePath);
            if(downloadCall != null) {
                downloadCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            File file = writeResponseBodyToDisk(response.body());
                            Uri uri = FileProvider.getUriForFile(PhotoActivity.this, "gzt.mtt.fileprovider", file);
                            share(uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }
        }

    }
}
