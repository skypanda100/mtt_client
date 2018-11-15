package gzt.mtt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import gzt.mtt.Constant;
import gzt.mtt.R;
import gzt.mtt.Util.TimeUtil;
import gzt.mtt.View.Daily.PhotoActivity;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DailyAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private JSONArray mDailies;
    private boolean mIsOneCol;
    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public DailyAdapter(Context context, boolean isOneCol) {
        this.mContext = context;
        this.mIsOneCol = isOneCol;
    }

    public void setDailies(JSONArray dailies) {
//        this.notifyDataSetChanged();
        boolean isRunAnimate = this.mDailies == null;
        this.mDailies = dailies;
        if (isRunAnimate) {
            if (this.mDailies != null) {
                notifyItemRangeInserted(0, this.mDailies.length());
            }
        } else {
            this.notifyDataSetChanged();
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        PhotoViewHolder photoViewHolder = null;
        if(this.mIsOneCol) {
            photoViewHolder = new PhotoViewHolder(LayoutInflater.from(this.mContext)
                    .inflate(R.layout.item_daily_one, viewGroup, false));
        } else {
            photoViewHolder = new PhotoViewHolder(LayoutInflater.from(this.mContext)
                    .inflate(R.layout.item_daily_three, viewGroup, false));
        }

        return photoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        PhotoViewHolder photoViewHolder = (PhotoViewHolder) viewHolder;

        photoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(i);
                }
            }
        });

        try{
            JSONObject daily = mDailies.getJSONObject(i);

            List<String> curDateChStrs = TimeUtil.date2chstr(new Date());
            List<String> images = new ArrayList<>();
            images.add(Constant.BaseImageUrl + daily.getString("imagePath"));
            String alias = daily.getString("alias");
            String avatar = daily.getString("avatar");
            String dateTime = daily.getString("dateTime");
            Date date = TimeUtil.str2date(dateTime, "yyyy-MM-dd HH:mm");
            List<String> dateChStrs = TimeUtil.date2chstr(date);
            String comment = daily.getString("comment");
            float grade = (float) daily.getDouble("grade");
            if(daily.has("others")) {
                JSONArray others = daily.getJSONArray("others");
                for(int index = 0;index < others.length();index++) {
                    images.add(Constant.BaseImageUrl + others.getJSONObject(index).getString("imagePath"));
                }
            }

            final Intent intent = new Intent(this.mContext, PhotoActivity.class);
            intent.putStringArrayListExtra("images", (ArrayList<String>) images);

            if(this.mIsOneCol) {
                // 清空
                photoViewHolder.mPhotoAppCompatImageView2.setImageDrawable(null);
                photoViewHolder.mPhotoAppCompatImageView2.setOnClickListener(null);
                photoViewHolder.mPhotoAppCompatImageView3.setImageDrawable(null);
                photoViewHolder.mPhotoAppCompatImageView3.setOnClickListener(null);

                for(int index = 0;index < 3 && index < images.size();index++) {
                    String imagePath = images.get(index);
                    switch (index) {
                        case 0:
                            Glide.with(this.mContext).load(imagePath).thumbnail(0.2f).into(photoViewHolder.mPhotoAppCompatImageView1);
                            photoViewHolder.mPhotoAppCompatImageView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    intent.putExtra("index", 0);
                                    mContext.startActivity(intent);
                                }
                            });
                            break;
                        case 1:
                            Glide.with(this.mContext).load(imagePath).thumbnail(0.2f).into(photoViewHolder.mPhotoAppCompatImageView2);
                            photoViewHolder.mPhotoAppCompatImageView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    intent.putExtra("index", 1);
                                    mContext.startActivity(intent);
                                }
                            });
                            break;
                        case 2:
                            Glide.with(this.mContext).load(imagePath).thumbnail(0.2f).into(photoViewHolder.mPhotoAppCompatImageView3);
                            photoViewHolder.mPhotoAppCompatImageView3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    intent.putExtra("index", 2);
                                    mContext.startActivity(intent);
                                }
                            });
                            break;
                    }
                }
                photoViewHolder.mGradeMaterialRatingBar.setRating(grade);
                photoViewHolder.mDateTextView1.setText(dateTime.substring(8, 10));
                photoViewHolder.mDateTextView2.setText(dateChStrs.get(1) + "/" + dateChStrs.get(3));
                if (!curDateChStrs.get(0).equals(dateChStrs.get(0))) {
                    photoViewHolder.mDateTextView3.setText(dateChStrs.get(0));
                } else {
                    photoViewHolder.mDateTextView3.setText("");
                }
                photoViewHolder.mCommentTextView.setText(comment);
                photoViewHolder.mTimeTextView.setText(dateTime.substring(11));
            } else {
                String imagePath = images.get(0);
                Glide.with(this.mContext).load(imagePath).thumbnail(0.2f).into(photoViewHolder.mPhotoAppCompatImageView1);
                photoViewHolder.mPhotoAppCompatImageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.putExtra("index", 0);
                        mContext.startActivity(intent);
                    }
                });
            }
            }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mDailies != null) {
            return mDailies.length();
        }
        return 0;
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        AvatarImageView mAvatarAvatarImageView;
        TextView mAliasTextView;
        AppCompatImageView mPhotoAppCompatImageView1;
        AppCompatImageView mPhotoAppCompatImageView2;
        AppCompatImageView mPhotoAppCompatImageView3;
        MaterialRatingBar mGradeMaterialRatingBar;
        TextView mDateTextView1;
        TextView mDateTextView2;
        TextView mDateTextView3;
        TextView mCommentTextView;
        TextView mTimeTextView;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            if(mIsOneCol) {
                this.mAvatarAvatarImageView = itemView.findViewById(R.id.avatar);
                this.mAliasTextView = itemView.findViewById(R.id.alias);
                this.mPhotoAppCompatImageView1 = itemView.findViewById(R.id.photo1);
                this.mPhotoAppCompatImageView2 = itemView.findViewById(R.id.photo2);
                this.mPhotoAppCompatImageView3 = itemView.findViewById(R.id.photo3);
                this.mGradeMaterialRatingBar = itemView.findViewById(R.id.grade);
                this.mDateTextView1 = itemView.findViewById(R.id.date1);
                this.mDateTextView2 = itemView.findViewById(R.id.date2);
                this.mDateTextView3 = itemView.findViewById(R.id.date3);
                this.mCommentTextView = itemView.findViewById(R.id.comment);
                this.mTimeTextView = itemView.findViewById(R.id.time);
                this.mPhotoAppCompatImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                this.mPhotoAppCompatImageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                this.mPhotoAppCompatImageView3.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                this.mPhotoAppCompatImageView1 = itemView.findViewById(R.id.photo);
                this.mPhotoAppCompatImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }
}
