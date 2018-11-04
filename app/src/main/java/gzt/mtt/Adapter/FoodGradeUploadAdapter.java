package gzt.mtt.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import gzt.mtt.R;

public class FoodGradeUploadAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<Object> mFoods;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public FoodGradeUploadAdapter(Context context) {
        this.mContext = context;
    }

    public void setFoods(List<Object> foods) {
        this.mFoods = foods;
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FoodsViewHolder foodsViewHolder = new FoodsViewHolder(LayoutInflater.from(this.mContext)
                .inflate(R.layout.item_food_grades_three, viewGroup, false));

        return foodsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        FoodsViewHolder foodsViewHolder = (FoodsViewHolder) viewHolder;

        foodsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(i);
                }
            }
        });
        Object food = mFoods.get(i);
        if (food instanceof Uri) {
            foodsViewHolder.mFoodAppCompatImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(this.mContext).load((Uri) food).into(foodsViewHolder.mFoodAppCompatImageView);
        } else {
            foodsViewHolder.mFoodAppCompatImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Picasso.with(this.mContext).load((int)food).resize(128, 128).centerInside().into(foodsViewHolder.mFoodAppCompatImageView);
        }
    }

    @Override
    public int getItemCount() {
        if(mFoods != null) {
            return mFoods.size();
        }
        return 0;
    }

    class FoodsViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mFoodAppCompatImageView;
        public FoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mFoodAppCompatImageView = itemView.findViewById(R.id.food);

        }
    }
}
