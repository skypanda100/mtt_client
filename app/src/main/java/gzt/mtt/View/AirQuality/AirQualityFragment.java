package gzt.mtt.View.AirQuality;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import gzt.mtt.Manager.HttpManager;
import gzt.mtt.R;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AirQualityFragment extends Fragment {

    private View mContainerView;
    private TextView mDateTimeTextView;
    private TextView mTempTextView;
    private MaterialRatingBar mTempRatingBar;
    private TextView mHumidityTextView;
    private MaterialRatingBar mHumidityRatingBar;
    private TextView mPm25TextView;
    private MaterialRatingBar mPm25RatingBar;
    private TextView mCo2TextView;
    private MaterialRatingBar mCo2RatingBar;
    private TextView mHchoTextView;
    private MaterialRatingBar mHchoRatingBar;

    public static AirQualityFragment newInstance() {
        return new AirQualityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.mContainerView = inflater.inflate(R.layout.fragment_air_quality, container, false);
        this.initData();
        this.initView();

        return this.mContainerView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initData() {
    }

    private void initView() {
        this.mDateTimeTextView = mContainerView.findViewById(R.id.dateTime);
        this.mTempTextView = mContainerView.findViewById(R.id.tempQuality);
        this.mTempRatingBar = mContainerView.findViewById(R.id.tempGrade);
        this.mHumidityTextView = mContainerView.findViewById(R.id.humidityQuality);
        this.mHumidityRatingBar = mContainerView.findViewById(R.id.humidityGrade);
        this.mPm25TextView = mContainerView.findViewById(R.id.pm25Quality);
        this.mPm25RatingBar = mContainerView.findViewById(R.id.pm25Grade);
        this.mCo2TextView = mContainerView.findViewById(R.id.co2Quality);
        this.mCo2RatingBar = mContainerView.findViewById(R.id.co2Grade);
        this.mHchoTextView = mContainerView.findViewById(R.id.hchoQuality);
        this.mHchoRatingBar = mContainerView.findViewById(R.id.hchoGrade);

        this.showLatestAirQuality();
    }

    private void showLatestAirQuality() {
        Call<ResponseBody> call = HttpManager.instance().get("serials/last");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject resJson = new JSONObject(response.body().string());
                    String dateTime = resJson.getString("dateTime");
                    mDateTimeTextView.setText(dateTime);
                    JSONObject grade = evaluateAirQuality(resJson);
                    mTempTextView.setText(grade.getString("tempQuality"));
                    mTempRatingBar.setRating((float)grade.getDouble("tempGrade"));
                    mHumidityTextView.setText(grade.getString("humidityQuality"));
                    mHumidityRatingBar.setRating((float)grade.getDouble("humidityGrade"));
                    mPm25TextView.setText(grade.getString("pm25Quality"));
                    mPm25RatingBar.setRating((float)grade.getDouble("pm25Grade"));
                    mCo2TextView.setText(grade.getString("co2Quality"));
                    mCo2RatingBar.setRating((float)grade.getDouble("co2Grade"));
                    mHchoTextView.setText(grade.getString("hchoQuality"));
                    mHchoRatingBar.setRating((float)grade.getDouble("hchoGrade"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private JSONObject evaluateAirQuality (JSONObject response) {
        JSONObject res = new JSONObject();
        try {
            double temp = response.getDouble("temp");
            double humidity = response.getDouble("humidity");
            double co2 = response.getDouble("co2");
            double pm25 = response.getDouble("pm2_5");
            double hcho = response.getDouble("hcho");

            int tempGrade = 5;
            int humidityGrade = 5;
            int co2Grade = 5;
            int pm25Grade = 5;
            int hchoGrade = 5;

            if (temp < 13 || temp > 30) {
                tempGrade = 0;
            } else if (temp < 15 || temp > 28) {
                tempGrade = 1;
            } else if (temp < 17 || temp > 26) {
                tempGrade = 2;
            } else if (temp < 19 || temp > 24) {
                tempGrade = 3;
            } else if (temp < 21 || temp > 22) {
                tempGrade = 4;
            } else {
                tempGrade = 5;
            }

            if (humidity < 10 || humidity > 75) {
                humidityGrade = 0;
            } else if (humidity < 20 || humidity > 72) {
                humidityGrade = 1;
            } else if (humidity < 30 || humidity > 69) {
                humidityGrade = 2;
            } else if (humidity < 35 || humidity > 66) {
                humidityGrade = 3;
            } else if (humidity < 40 || humidity > 63) {
                humidityGrade = 4;
            } else {
                humidityGrade = 5;
            }

            if (co2 < 250 || co2 >= 2000) {
                co2Grade = 0;
            } else if (co2 >= 1500 && co2 < 2000) {
                co2Grade = 1;
            } else if (co2 >= 1300 && co2 < 1500) {
                co2Grade = 2;
            } else if (co2 >= 1000 && co2 < 1300) {
                co2Grade = 3;
            } else if (co2 >= 350 && co2 < 1000) {
                co2Grade = 4;
            } else {
                co2Grade = 5;
            }

            if (pm25 >= 250) {
                pm25Grade = 0;
            } else if (pm25 >= 150 && pm25 < 250) {
                pm25Grade = 1;
            } else if (pm25 >= 115 && pm25 < 150) {
                pm25Grade = 2;
            } else if (pm25 >= 75 && pm25 < 115) {
                pm25Grade = 3;
            } else if (pm25 >= 35 && pm25 < 75) {
                pm25Grade = 4;
            } else {
                pm25Grade = 5;
            }

            if (hcho >= 0.08) {
                hchoGrade = 0;
            } else if (hcho >= 0.07) {
                hchoGrade = 1;
            } else if (hcho >= 0.06) {
                hchoGrade = 2;
            } else if (hcho >= 0.05) {
                hchoGrade = 3;
            } else if (hcho >= 0.04) {
                hchoGrade = 4;
            } else {
                hchoGrade = 5;
            }

            res.put("tempGrade", tempGrade);
            res.put("humidityGrade", humidityGrade);
            res.put("co2Grade", co2Grade);
            res.put("pm25Grade", pm25Grade);
            res.put("hchoGrade", hchoGrade);
            res.put("tempQuality", temp);
            res.put("humidityQuality", humidity);
            res.put("co2Quality", co2);
            res.put("pm25Quality", pm25);
            res.put("hchoQuality", hcho);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }
}
