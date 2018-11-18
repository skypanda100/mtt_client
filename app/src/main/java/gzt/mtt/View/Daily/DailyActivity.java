package gzt.mtt.View.Daily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.scwang.smartrefresh.header.TaurusHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gzt.mtt.Adapter.DailyAdapter;
import gzt.mtt.Animator.FlyItemAnimator;
import gzt.mtt.BaseActivity;
import gzt.mtt.Constant;
import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyActivity extends BaseActivity implements PopupMenu.OnMenuItemClickListener, OnRefreshListener, OnLoadMoreListener {
    private static final int REQUEST_CODE_DAILY = 0;
    private RefreshLayout mDailyRefreshLayout;
    private RecyclerView mDailyRecyclerView;
    private DailyAdapter mDailyAdapter;
    private JSONArray mDailies;
    private JSONArray mUsers;
    private boolean mIsOneCol = true;

    private List<String> mSorts;
    private List<String> mFilters;
    private int mPageCount = 1;
    private int mFetchCount = 30;
    private String mSort = "-dateTime";
    private String mFilter = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initData();
        this.initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.daily, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_view) {
            this.changeView(item);
        } else if (id == R.id.action_sort) {
            this.mSorts = Arrays.asList(this.getResources().getStringArray(R.array.sort_array));
            this.showPopupMenu(this.mSorts, R.id.action_sort);
        } else if (id == R.id.action_filter) {
            this.mFilters = Arrays.asList(this.getResources().getStringArray(R.array.type_array));
            this.showPopupMenu(this.mFilters, R.id.action_filter);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (this.mSorts != null) {
            int sortIndex = this.mSorts.indexOf(item.getTitle());
            switch (sortIndex) {
                case 0:
                    if (this.mSort.equals("-dateTime")) {
                        this.mSort = "dateTime";
                    } else {
                        this.mSort = "-dateTime";
                    }
                    break;
                case 1:
                    if (this.mSort.equals("-grade")) {
                        this.mSort = "grade";
                    } else {
                        this.mSort = "-grade";
                    }
                    break;
            }
        }

        if (this.mFilters != null) {
            int filterIndex = this.mFilters.indexOf(item.getTitle());
            if(filterIndex > -1) {
                this.mFilter = filterIndex == this.mFilters.size() - 1 ? "" : this.mFilters.get(filterIndex);
            }
        }

        this.fetchData();

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DAILY) {
            this.fetchData();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        fetchData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        this.mPageCount++;
        fetchData();
    }

    private void initData() {
        this.fetchData();
    }

    private void initView() {
        this.setContentView(R.layout.activity_daily);

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ) {
            getSupportActionBar().setTitle("生活点滴");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        this.mDailyRefreshLayout = this.findViewById(R.id.daily_refresh);
        // header
        TaurusHeader taurusHeader = new TaurusHeader(this);
        taurusHeader.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
        this.mDailyRefreshLayout.setRefreshHeader(taurusHeader);
        // foot
        BallPulseFooter ballPulseFooter = new BallPulseFooter(this);
        ballPulseFooter.setSpinnerStyle(SpinnerStyle.Scale);
        ballPulseFooter.setAnimatingColor(this.getResources().getColor(R.color.colorPrimary));
        this.mDailyRefreshLayout.setRefreshFooter(ballPulseFooter);

        this.mDailyRefreshLayout.setOnRefreshListener(this);
        this.mDailyRefreshLayout.setOnLoadMoreListener(this);

        this.mDailyRecyclerView = this.findViewById(R.id.daily);
        this.mDailyRecyclerView.setItemAnimator(new FlyItemAnimator());
        this.setLayoutManagerPolicy(1);
    }

    private void setLayoutManagerPolicy(int cols) {
        this.mDailyRecyclerView.setLayoutManager(new GridLayoutManager(this, cols));
        this.mDailyRecyclerView.setAdapter(mDailyAdapter = new DailyAdapter(this, cols == 1));
        this.mDailyAdapter.setDailies(this.mDailies);
        this.mDailyAdapter.setItemClickListener(new DailyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    JSONObject daily = mDailies.getJSONObject(position);
                    ArrayList<String> images = new ArrayList<>();
                    images.add(Constant.BaseImageUrl + daily.getString("imagePath"));
                    if(daily.has("others")){
                        JSONArray others = daily.getJSONArray("others");
                        for(int i = 0;i < others.length();i++) {
                            images.add(Constant.BaseImageUrl + others.getJSONObject(i).getString("imagePath"));
                        }
                    }

                    Intent intent = new Intent(DailyActivity.this, DailyUploadActivity.class);
                    intent.putExtra("id", daily.getString("_id"));
                    intent.putExtra("alias", daily.getString("alias"));
                    intent.putExtra("avatar", daily.getString("avatar"));
                    intent.putExtra("type", daily.getString("type"));
                    intent.putExtra("address", daily.getString("address"));
                    intent.putExtra("dateTime", daily.getString("dateTime"));
                    intent.putExtra("comment", daily.getString("comment"));
                    intent.putExtra("grade", (float)daily.getDouble("grade"));
                    intent.putStringArrayListExtra("images", images);
                    startActivity(intent, REQUEST_CODE_DAILY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void fetchData() {
        Call<ResponseBody> allUsersCall = HttpManager.instance().get("users/all");
        if(allUsersCall != null) {
            allUsersCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONArray resJsonArray = new JSONArray(response.body().string());
                        onFetchAllUsersSuccess(resJsonArray);

                        Map<String, String> options = new HashMap<>();
                        options.put("filter", mFilter);
                        options.put("sort", mSort);
                        options.put("limit", String.valueOf(mPageCount * mFetchCount));

                        Call<ResponseBody> dailyCall = HttpManager.instance().get("dailies", options);
                        if(dailyCall != null) {
                            dailyCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        JSONArray resJsonArray = new JSONArray(response.body().string());
                                        onFetchDailySuccess(resJsonArray);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onFetchDailyFailed("some errors happened in server");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    onFetchDailyFailed("some errors happened in server");
                                }
                            });
                        } else {
                            onFetchDailyFailed("some errors happened in client");
                        }
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
            onFetchAllUsersFailed("some errors happened in client");
        }
    }

    private void onFetchAllUsersSuccess(JSONArray jsonArray) {
        this.mUsers = jsonArray;
    }

    private void onFetchAllUsersFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void onFetchDailySuccess(JSONArray jsonArray) {
        this.mDailyRefreshLayout.finishRefresh(1000);
        this.mDailyRefreshLayout.finishLoadMore(1000);

        try {
            for(int i = 0;i < jsonArray.length();i++) {
                JSONObject daily = jsonArray.getJSONObject(i);
                JSONObject user = this.getUser(daily.getString("user"));
                daily.put("alias", user.getString("alias"));
                daily.put("avatar", user.getString("avatar"));
            }
            this.mDailies = jsonArray;
            if(this.mDailyAdapter != null) {
                this.mDailyAdapter.setDailies(this.mDailies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onFetchDailyFailed(String message) {
        this.mDailyRefreshLayout.finishRefresh(1000, false);
        this.mDailyRefreshLayout.finishLoadMore(1000);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void changeView(MenuItem item) {
        if(this.mIsOneCol) {
            item.setIcon(R.drawable.col_one);
            this.setLayoutManagerPolicy(3);
        } else {
            item.setIcon(R.drawable.col_three);
            this.setLayoutManagerPolicy(1);
        }
        this.mIsOneCol = !this.mIsOneCol;
    }

    private void showPopupMenu(List<String> menus, int resId) {
        PopupMenu popup = new PopupMenu(this, this.findViewById(resId));
        Menu menu = popup.getMenu();
        for(String m : menus) {
            menu.add(m);
        }
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }
}
