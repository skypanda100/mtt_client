package gzt.mtt.View.FoodGrade;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import gzt.mtt.Adapter.FoodGradeUploadAdapter;
import gzt.mtt.BaseActivity;
import gzt.mtt.Constant;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import gzt.mtt.Util.PathUtil;
import gzt.mtt.Util.TimeUtil;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Response;
import top.zibin.luban.Luban;

public class FoodGradeUploadActivity extends BaseActivity {
    private static final int REQUEST_CODE_CHOOSE = 0;
    private static final int REQUEST_CODE_DELETE = 1;

    private boolean mIsAdd;
    private String mId;
    private String mAlias;
    private String mAvatar;
    private String mDateTime;
    private String mComment;
    private float mGrade;
    private List<String> mImages;

    private List<Object> mFoods = new ArrayList<>();
    private RecyclerView mFoodsRecyclerView;
    private FoodGradeUploadAdapter mFoodGradeUploadAdapter;
    private MaterialRatingBar mGradeRatingBar;
    private EditText mCommentEditText;
    private SweetAlertDialog mWaitingDialog;
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
        getMenuInflater().inflate(R.menu.food_grade_upload, menu);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> selected = Matisse.obtainResult(data);
//            this.initFoods();
            for (int i = 0;i < selected.size();i++) {
                this.mFoods.add(this.mFoods.size() - 1, selected.get(i));
            }
            this.mFoodGradeUploadAdapter.setFoods(this.mFoods);
        } else if (requestCode == REQUEST_CODE_DELETE && resultCode == RESULT_OK) {
            int index = data.getIntExtra("index", -1);
            this.mFoods.remove(index);
            this.mFoodGradeUploadAdapter.setFoods(this.mFoods);
        }
    }

    private void initFoods() {
        this.mFoods.clear();
        if (this.mImages != null) {
            for(String image : this.mImages) {
                this.mFoods.add(image);
            }
        }
        this.mFoods.add(R.drawable.plus);
    }

    private void initData() {
        Intent intent = this.getIntent();
        this.mId = intent.getStringExtra("id");
        this.mAlias = intent.getStringExtra("alias");
        this.mAvatar = intent.getStringExtra("avatar");
        this.mDateTime = intent.getStringExtra("dateTime");
        this.mComment = intent.getStringExtra("comment");
        this.mGrade = intent.getFloatExtra("grade", 0.0f);
        this.mImages = intent.getStringArrayListExtra("images");

        this.initFoods();

        if (this.mId == null || this.mId.equals("")) {
            this.mIsAdd = true;
        } else {
            this.mIsAdd = false;
        }
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

        this.mFoodsRecyclerView = this.findViewById(R.id.foods);
        this.mFoodsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.mFoodsRecyclerView.setAdapter(mFoodGradeUploadAdapter = new FoodGradeUploadAdapter(this));
        this.mFoodGradeUploadAdapter.setItemClickListener(new FoodGradeUploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mFoods.size() - 1 == position) {
                    openGallery();
                } else {
                    List<String> images = getStringImages();
                    Intent intent = new Intent(FoodGradeUploadActivity.this, FoodGradeActivity.class);
                    intent.putStringArrayListExtra("images", (ArrayList<String>) images);
                    intent.putExtra("index", position);
                    intent.putExtra("canDelete", true);
                    startActivity(intent, REQUEST_CODE_DELETE);
                }
            }
        });
        this.mFoodGradeUploadAdapter.setFoods(this.mFoods);

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
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private List<String> getStringImages() {
        List<String> images = new ArrayList<>();
        for(int i = 0;i < this.mFoods.size() - 1;i++) {
            Object food = this.mFoods.get(i);
            if (food instanceof Uri) {
                images.add(PathUtil.uri2path(this, (Uri) food));
            } else {
                images.add((String) food);
            }
        }
        return images;
    }

    private List<Object> getObjectImages() {
        List<Object> images = new ArrayList<>();
        for(int i = 0;i < this.mFoods.size() - 1;i++) {
            Object food = this.mFoods.get(i);
            images.add(food);
        }
        return images;
    }

    private void handleActionDoneClicked() {
        if(!this.validate()) {
            return;
        }
        this.mWaitingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        this.mWaitingDialog.setCancelable(false);

        List<Object> images = this.getObjectImages();
        String user = (String) this.mStorageManager.getSharedPreference("userName", "");
        float grade = this.mGradeRatingBar.getRating();
        String comment = this.mCommentEditText.getText().toString();
        String dateTime = this.mIsAdd ? "" : this.mDateTime;
        if (this.mIsAdd) {
            String path = PathUtil.uri2path(this, (Uri) images.get(0));
            File homeFile = new File(path);
            if(homeFile.exists() && homeFile.isFile()){
                dateTime = TimeUtil.date2str(new Date(homeFile.lastModified()), "yyyy-MM-dd HH:mm");
            } else {
                dateTime = TimeUtil.date2str(new Date(), "yyyy-MM-dd HH:mm");
            }
        }

        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(mId, user, grade, comment, dateTime, images);
    }

    private boolean validate() {
        if (this.mId != null && !this.mId.equals("")) {
            return true;
        } else {
            if(this.mFoods.size() > 1) {
                return true;
            } else {
                this.onUploadFailed("请添加图片");
                return false;
            }
        }
    }

    private void onUploadSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onUploadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class UploadTask extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWaitingDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                publishProgress(0);
                String id = objects[0] == null ? "" : (String) objects[0];
                String user = (String) objects[1];
                float grade = (float) objects[2];
                String comment = (String) objects[3];
                String dateTime = (String) objects[4];
                String imagePath = null;
                List<Object> images = (ArrayList<Object>) objects[5];
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
                        localImages.add(PathUtil.uri2path(FoodGradeUploadActivity.this, (Uri) image));
                        if (i == 0) {
                            isLocalImageFirst = true;
                        }
                    }
                }

                List<File> compressImages = Luban.with(FoodGradeUploadActivity.this).load(localImages).get();
                publishProgress(1);
                Map<String, Object> params = new HashMap<>();
                params.put("id", id);
                params.put("user", user);
                params.put("grade", grade);
                params.put("comment", comment);
                params.put("dateTime", dateTime);
                params.put("images", netImages);
                if (imagePath != null) {
                    params.put("imagePath", imagePath);
                }
                for(int i = 0;i < compressImages.size();i++) {
                    int offset = isLocalImageFirst ? 0 : 10;
                    File image = compressImages.get(i);
                    params.put(image.getName() + "-" + (i + offset), image);
                }
                Response<ResponseBody> response = HttpManager.instance().put("foodGrades", params).execute();
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
                    mWaitingDialog.setContentText("图片压缩中...");
                    break;
                case 1:
                    mWaitingDialog.setContentText("图片上传中...");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            boolean isSuccess = (boolean) o;
            if (isSuccess) {
                mWaitingDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mWaitingDialog.setContentText("提交评分成功");
            } else {
                mWaitingDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                mWaitingDialog.setContentText("提交评分失败");
            }
        }
    }
}
