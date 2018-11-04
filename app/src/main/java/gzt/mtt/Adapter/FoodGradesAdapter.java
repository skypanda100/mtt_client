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

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gzt.mtt.Constant;
import gzt.mtt.R;
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
        this.notifyDataSetChanged();
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

            String imagePath = foodGrade.getString("imagePath");
            String alias = foodGrade.getString("alias");
            String avatar = foodGrade.getString("avatar");
            String dateTime = foodGrade.getString("dateTime");
            String comment = foodGrade.getString("comment");
            int grade = foodGrade.getInt("grade");

            if(this.mIsOneCol) {
                Picasso.with(this.mContext).load(Constant.BaseImageUrl + avatar).into(foodGradesViewHolder.mAvatarAvatarImageView);
                foodGradesViewHolder.mAliasTextView.setText(alias);
                Picasso.with(this.mContext).load(Constant.BaseImageUrl + imagePath).into(foodGradesViewHolder.mFoodAppCompatImageView);
                foodGradesViewHolder.mGradeMaterialRatingBar.setRating(grade);
                foodGradesViewHolder.mDateTimeTextView.setText(dateTime);
                foodGradesViewHolder.mCommentTextView.setText(comment);
            } else {
                int width = 200;
                int height = 200;
                Picasso.with(this.mContext).load(Constant.BaseImageUrl + imagePath).resize(width, height).centerCrop().into(foodGradesViewHolder.mFoodAppCompatImageView);
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
        AppCompatImageView mFoodAppCompatImageView;
        MaterialRatingBar mGradeMaterialRatingBar;
        TextView mDateTimeTextView;
        TextView mCommentTextView;
        public FoodGradesViewHolder(@NonNull View itemView) {
            super(itemView);
            if(mIsOneCol) {
                this.mAvatarAvatarImageView = itemView.findViewById(R.id.avatar);
                this.mAliasTextView = itemView.findViewById(R.id.alias);
                this.mFoodAppCompatImageView = itemView.findViewById(R.id.food);
                this.mGradeMaterialRatingBar = itemView.findViewById(R.id.grade);
                this.mDateTimeTextView = itemView.findViewById(R.id.dateTime);
                this.mCommentTextView = itemView.findViewById(R.id.comment);
            } else {
                this.mFoodAppCompatImageView = itemView.findViewById(R.id.food);
            }
            this.mFoodAppCompatImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
