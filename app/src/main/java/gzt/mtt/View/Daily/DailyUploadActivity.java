package gzt.mtt.View.Daily;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import gzt.mtt.Adapter.DailyUploadAdapter;
import gzt.mtt.Component.WatingDialog.WaitingDialog;
import gzt.mtt.Constant;
import gzt.mtt.Engine.MatisseEngine;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import gzt.mtt.Util.PathUtil;
import gzt.mtt.Util.PhotoUtil;
import gzt.mtt.View.BaseActivity;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Response;
import top.zibin.luban.Luban;

public class DailyUploadActivity extends BaseActivity implements TagView.OnTagClickListener {
    private static final int REQUEST_CODE_CHOOSE = 0;
    private static final int REQUEST_CODE_DELETE = 1;

    private boolean mIsAdd;
    private String mId;
    private String mUser;
    private String mAlias;
    private String mAvatar;
    private String mType;
    private String mAddress;
    private String mDateTime;
    private String mComment;
    private float mGrade;
    private List<String> mImages;
    private List<String> mTypes;
    private List<String> mTags;
    private List<String> mCheckedTags;

    private List<Object> mPhotos = new ArrayList<>();
    private RecyclerView mPhotoRecyclerView;
    private DailyUploadAdapter mDailyUploadAdapter;
    private TextView mTypeTextView;
    private TextView mAddressTextView;
    private TagContainerLayout mTagContainerLayout;
    private MaterialRatingBar mGradeRatingBar;
    private EditText mCommentEditText;
    private WaitingDialog mWaitingDialog;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily_upload, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            this.handleActionDoneClicked();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTagClick(int position, String text) {
        TagView tagView = this.mTagContainerLayout.getTagView(position);
        if (this.mCheckedTags.contains(text)) {
            this.mCheckedTags.remove(text);
            this.setTagUnChecked(tagView);
        } else {
            this.mCheckedTags.add(text);
            this.setTagChecked(tagView);
        }
        Log.d("zdt", this.mCheckedTags.toString());
    }

    @Override
    public void onTagLongClick(int position, String text) {

    }

    @Override
    public void onTagCrossClick(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);

            if(this.mPhotos.size() == 1 && selected.size() > 0) {
                String path = PathUtil.uri2path(this, selected.get(0));
                String address = PhotoUtil.getAddress(this, path);
                this.mAddressTextView.setText(address == null ? "" : address);
            }

            for (int i = 0;i < selected.size();i++) {
                this.mPhotos.add(this.mPhotos.size() - 1, selected.get(i));
            }
            this.mDailyUploadAdapter.setPhotos(this.mPhotos);
        } else if (requestCode == REQUEST_CODE_DELETE && resultCode == RESULT_OK) {
            int index = data.getIntExtra("index", -1);
            this.mPhotos.remove(index);
            this.mDailyUploadAdapter.setPhotos(this.mPhotos);
        }
    }

    private void initPhotos() {
        this.mPhotos.clear();
        if (this.mImages != null) {
            for(String image : this.mImages) {
                this.mPhotos.add(image);
            }
        }
        this.mPhotos.add(R.drawable.add);
    }

    private void initData() {
        this.mTags = new ArrayList<>();
        this.mTags.add("鸡肉");
        this.mTags.add("鸭肉");
        this.mTags.add("鱼肉");
        this.mTags.add("猪肉");
        this.mTags.add("牛肉");
        this.mTags.add("羊肉");
        this.mTags.add("面条");
        this.mTags.add("包子");
        this.mTags.add("馒头");
        this.mTags.add("垃圾食品");
        this.mTags.add("绿色蔬菜");

        Intent intent = this.getIntent();
        this.mId = intent.getStringExtra("id");
        this.mAlias = intent.getStringExtra("alias");
        this.mAvatar = intent.getStringExtra("avatar");
        this.mType = intent.getStringExtra("type");
        this.mAddress = intent.getStringExtra("address");
        this.mDateTime = intent.getStringExtra("dateTime");
        this.mComment = intent.getStringExtra("comment");
        this.mGrade = intent.getFloatExtra("grade", 0.0f);
        this.mImages = intent.getStringArrayListExtra("images");
        this.mCheckedTags = intent.getStringArrayListExtra("tags");

        if (this.mCheckedTags == null) {
            this.mCheckedTags = new ArrayList<>();
        }
        String[] typeArray = getResources().getStringArray(R.array.type_array);
        this.mTypes = new ArrayList<>();
        for(int i = 0;i < typeArray.length - 1;i++) {
            this.mTypes.add(typeArray[i]);
        }
        if(this.mType == null && this.mTypes.size() > 0) {
            this.mType = this.mTypes.get(0);
        }

        this.initPhotos();

        if (this.mId == null || this.mId.equals("")) {
            this.mIsAdd = true;
        } else {
            this.mIsAdd = false;
        }
    }

    private void initView() {
        this.setContentView(R.layout.activity_daily_upload);

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("生活记录");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        this.mWaitingDialog = new WaitingDialog();
        this.mWaitingDialog.setCancelable(false);

        this.mPhotoRecyclerView = this.findViewById(R.id.photos);
        this.mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.mPhotoRecyclerView.setAdapter(mDailyUploadAdapter = new DailyUploadAdapter(this));
        this.mDailyUploadAdapter.setItemClickListener(new DailyUploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mPhotos.size() - 1 == position) {
                    openGallery();
                } else {
                    List<String> images = getStringImages();
                    Intent intent = new Intent(DailyUploadActivity.this, PhotoActivity.class);
                    intent.putStringArrayListExtra("images", (ArrayList<String>) images);
                    intent.putExtra("index", position);
                    intent.putExtra("canDelete", true);
                    startActivity(intent, REQUEST_CODE_DELETE);
                }
            }
        });
        this.mDailyUploadAdapter.setPhotos(this.mPhotos);

        this.mTypeTextView = this.findViewById(R.id.type);
        this.mTypeTextView.setText(this.mType);
        this.mTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(DailyUploadActivity.this, DailyUploadActivity.this.findViewById(R.id.type));
                Menu menu = popup.getMenu();
                for(String t : mTypes) {
                    menu.add(t);
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        DailyUploadActivity.this.mTypeTextView.setText(item.getTitle());
                        return false;
                    }
                });
                popup.show();
            }
        });
        this.mAddressTextView = this.findViewById(R.id.address);
        this.mAddressTextView.setText(this.mAddress);
        this.mTagContainerLayout = this.findViewById(R.id.tag);
        this.mTagContainerLayout.setOnTagClickListener(this);
        this.mTagContainerLayout.setTags(this.mTags);
        for (String tag : this.mCheckedTags) {
            int index = this.mTags.indexOf(tag);
            Log.d("zdt", index + "-" + tag);
            if (index > -1) {
                this.setTagChecked(this.mTagContainerLayout.getTagView(index));
            }
        }
        this.mGradeRatingBar = this.findViewById(R.id.grade);
        this.mGradeRatingBar.setRating(this.mGrade);
        this.mCommentEditText = this.findViewById(R.id.comment);
        this.mCommentEditText.setText(this.mComment);
    }

    private void openGallery () {
        Matisse.from(this)
                .choose(MimeType.ofAll())
                .theme(R.style.GalleryTheme)
                .countable(true)
//                .capture(true)
//                .captureStrategy(new CaptureStrategy(true, "gzt.mtt.fileprovider"))
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new MatisseEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private List<String> getStringImages() {
        List<String> images = new ArrayList<>();
        for(int i = 0;i < this.mPhotos.size() - 1;i++) {
            Object photo = this.mPhotos.get(i);
            if (photo instanceof Uri) {
                images.add(PathUtil.uri2path(this, (Uri) photo));
            } else {
                images.add((String) photo);
            }
        }
        return images;
    }

    private List<Object> getObjectImages() {
        List<Object> images = new ArrayList<>();
        for(int i = 0;i < this.mPhotos.size() - 1;i++) {
            Object photo = this.mPhotos.get(i);
            images.add(photo);
        }
        return images;
    }

    private void handleActionDoneClicked() {
        if(!this.validate()) {
            return;
        }

        final List<Object> images = this.getObjectImages();
        this.mUser = (String) this.mStorageManager.getSharedPreference("userName", "");
        this.mType = this.mTypeTextView.getText().toString();
        this.mGrade = this.mGradeRatingBar.getRating();
        this.mComment = this.mCommentEditText.getText().toString();
        this.mAddress = this.mAddressTextView.getText().toString();
        this.mDateTime = this.mIsAdd ? "" : this.mDateTime;
        if (this.mIsAdd) {
            String path = PathUtil.uri2path(this, (Uri) images.get(0));
            this.mDateTime = PhotoUtil.getTime(path);
        }
        this.mWaitingDialog.show(getSupportFragmentManager(), "");

        // 如果不延迟的话，这个dialog背景不会模糊，不清楚为什么
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UploadTask uploadTask = new UploadTask();
                uploadTask.execute(images);
            }
        }, 1000);
    }

    private boolean validate() {
        if (this.mId != null && !this.mId.equals("")) {
            return true;
        } else {
            if(this.mPhotos.size() > 1) {
                return true;
            } else {
                this.onUploadFailed("请添加图片");
                return false;
            }
        }
    }

    private void onUploadSuccess(String message) {
        if (this.mWaitingDialog.getDialog() != null) {
            this.mWaitingDialog.dismiss();
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        this.finish();
    }

    private void onUploadFailed(String message) {
        if (this.mWaitingDialog.getDialog() != null) {
            this.mWaitingDialog.dismiss();
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setTagChecked(TagView tagView) {
        tagView.setTagTextColor(getResources().getColor(R.color.colorAccent));
        tagView.setTagBorderColor(getResources().getColor(R.color.colorAccent));
    }

    private void setTagUnChecked(TagView tagView) {
        tagView.setTagTextColor(getResources().getColor(R.color.fontGray));
        tagView.setTagBorderColor(getResources().getColor(R.color.fontGray));
    }

    private class UploadTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                publishProgress(0);
                String id = mId == null ? "" : mId;
                String user = mUser;
                String type = mType;
                String address = mAddress;
                float grade = mGrade;
                String comment = mComment;
                String dateTime = mDateTime;
                String imagePath = null;
                JSONArray tags = new JSONArray();
                Log.d("zdt", mCheckedTags.toString());
                for (String tag : mCheckedTags) {
                    tags.put(tag);
                }
                List<Object> images = (ArrayList<Object>) objects[0];
                JSONArray netImages = new JSONArray();
                List<String> localImages = new ArrayList<>();
                boolean isLocalImageFirst = false;
                for (int i = 0;i < images.size();i++) {
                    Object image = images.get(i);
                    if (image instanceof String) {
                        String path = ((String) image).replace(Constant.BaseImageUrl, "");
                        if (i == 0) {
                            isLocalImageFirst = false;
                            imagePath = path;
                        } else {
                            netImages.put(path);
                        }
                    } else {
                        localImages.add(PathUtil.uri2path(DailyUploadActivity.this, (Uri) image));
                        if (i == 0) {
                            isLocalImageFirst = true;
                        }
                    }
                }

                List<File> compressImages = Luban.with(DailyUploadActivity.this).load(localImages).get();
                publishProgress(1);
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("user", user);
                params.put("grade", grade);
                params.put("comment", comment);
                params.put("dateTime", dateTime);
                params.put("images", netImages);
                params.put("address", address);
                params.put("type", type);
                params.put("tags", tags);

                if (imagePath != null) {
                    params.put("imagePath", imagePath);
                }
                for(int i = 0;i < compressImages.size();i++) {
                    int offset = isLocalImageFirst ? 0 : 10;
                    File image = compressImages.get(i);
                    params.put(image.getName() + "-" + (i + offset), image);
                }
                Response<ResponseBody> response = HttpManager.instance().put("dailies", params).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                Log.d("zdt", jsonObject.getString("message"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            int value = (int) values[0];
            switch (value){
                case 0:
//                    mWaitingDialog.setContentText("图片压缩中...");
                    break;
                case 1:
//                    mWaitingDialog.setContentText("图片上传中...");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            boolean isSuccess = (boolean) o;
            if (isSuccess) {
                onUploadSuccess("提交评分成功");
            } else {
                onUploadFailed("提交评分失败");
            }
        }
    }
}
