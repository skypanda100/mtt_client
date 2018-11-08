package gzt.mtt.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gzt.mtt.Constant;
import gzt.mtt.R;
import gzt.mtt.Util.TimeUtil;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FoodGradesAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private JSONArray mFoodGrades;
    private boolean mIsOneCol;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public FoodGradesAdapter(Context context, boolean isOneCol) {
        this.mContext = context;
        this.mIsOneCol = isOneCol;
    }

    public void setFoodGrades(JSONArray foodGrades) {
        this.mFoodGrades = foodGrades;
        // 这个方法不会触发动画
//        this.notifyDataSetChanged();
        if(this.mFoodGrades != null) {
            notifyItemRangeInserted(0, this.mFoodGrades.length());
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FoodGradesViewHolder foodGradesViewHolder = null;
        if(this.mIsOneCol) {
            foodGradesViewHolder = new FoodGradesViewHolder(LayoutInflater.from(this.mContext)
                    .inflate(R.layout.item_food_grades_one, viewGroup, false));
        } else {
            foodGradesViewHolder = new FoodGradesViewHolder(LayoutInflater.from(this.mContext)
                    .inflate(R.layout.item_food_grades_three, viewGroup, false));
        }

        return foodGradesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        FoodGradesViewHolder foodGradesViewHolder = (FoodGradesViewHolder) viewHolder;

        foodGradesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(i);
                }
            }
        });

        try{
            JSONObject foodGrade = mFoodGrades.getJSONObject(i);

            List<String> images = new ArrayList<>();
            images.add(foodGrade.getString("imagePath"));
            String alias = foodGrade.getString("alias");
            String avatar = foodGrade.getString("avatar");
            String dateTime = foodGrade.getString("dateTime");
            Date date = TimeUtil.str2date(dateTime, "yyyy-MM-dd HH:mm");
            List<String> dateChStrs = TimeUtil.date2chstr(date);
            String comment = foodGrade.getString("comment");
            int grade = foodGrade.getInt("grade");
            if(foodGrade.has("others")) {
                JSONArray others = foodGrade.getJSONArray("others");
                for(int index = 0;index < others.length();index++) {
                    images.add(others.getJSONObject(index).getString("imagePath"));
                }
            }

            int width = 200;
            int height = 200;
            if(this.mIsOneCol) {
//                Picasso.with(this.mContext).load(Constant.BaseImageUrl + avatar).into(foodGradesViewHolder.mAvatarAvatarImageView);
//                foodGradesViewHolder.mAliasTextView.setText(alias);
                // 清空
                foodGradesViewHolder.mFoodAppCompatImageView2.setImageDrawable(null);
                foodGradesViewHolder.mFoodAppCompatImageView3.setImageDrawable(null);
                for(int index = 0;index < 3 && index < images.size();index++) {
                    String imagePath = images.get(index);
                    switch (index) {
                        case 0:
                            Picasso.with(this.mContext).load(Constant.BaseImageUrl + imagePath).resize(width, height).centerCrop().into(foodGradesViewHolder.mFoodAppCompatImageView1);
                            break;
                        case 1:
                            Picasso.with(this.mContext).load(Constant.BaseImageUrl + imagePath).resize(width, height).centerCrop().into(foodGradesViewHolder.mFoodAppCompatImageView2);
                            break;
                        case 2:
                            Picasso.with(this.mContext).load(Constant.BaseImageUrl + imagePath).resize(width, height).centerCrop().into(foodGradesViewHolder.mFoodAppCompatImageView3);
                            break;
                    }
                }
                foodGradesViewHolder.mGradeMaterialRatingBar.setRating(grade);
                foodGradesViewHolder.mDateTextView1.setText(dateTime.substring(8, 10));
                foodGradesViewHolder.mDateTextView2.setText(dateChStrs.get(1) + "/" + dateChStrs.get(3));
                foodGradesViewHolder.mCommentTextView.setText(comment);
                foodGradesViewHolder.mTimeTextView.setText(dateTime.substring(11));
            } else {
                String imagePath = images.get(0);
                Picasso.with(this.mContext).load(Constant.BaseImageUrl + imagePath).resize(width, height).centerCrop().into(foodGradesViewHolder.mFoodAppCompatImageView1);
            }
            }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mFoodGrades != null) {
            return mFoodGrades.length();
        }
        return 0;
    }

    class FoodGradesViewHolder extends RecyclerView.ViewHolder {
        AvatarImageView mAvatarAvatarImageView;
        TextView mAliasTextView;
        AppCompatImageView mFoodAppCompatImageView1;
        AppCompatImageView mFoodAppCompatImageView2;
        AppCompatImageView mFoodAppCompatImageView3;
        MaterialRatingBar mGradeMaterialRatingBar;
        TextView mDateTextView1;
        TextView mDateTextView2;
        TextView mCommentTextView;
        TextView mTimeTextView;
        public FoodGradesViewHolder(@NonNull View itemView) {
            super(itemView);
            if(mIsOneCol) {
                this.mAvatarAvatarImageView = itemView.findViewById(R.id.avatar);
                this.mAliasTextView = itemView.findViewById(R.id.alias);
                this.mFoodAppCompatImageView1 = itemView.findViewById(R.id.food1);
                this.mFoodAppCompatImageView2 = itemView.findViewById(R.id.food2);
                this.mFoodAppCompatImageView3 = itemView.findViewById(R.id.food3);
                this.mGradeMaterialRatingBar = itemView.findViewById(R.id.grade);
                this.mDateTextView1 = itemView.findViewById(R.id.date1);
                this.mDateTextView2 = itemView.findViewById(R.id.date2);
                this.mCommentTextView = itemView.findViewById(R.id.comment);
                this.mTimeTextView = itemView.findViewById(R.id.time);
                this.mFoodAppCompatImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                this.mFoodAppCompatImageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                this.mFoodAppCompatImageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                this.mFoodAppCompatImageView1 = itemView.findViewById(R.id.food);
                this.mFoodAppCompatImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }
}
