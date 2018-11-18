package gzt.mtt.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import gzt.mtt.Manager.ImageManager;
import gzt.mtt.R;

public class DailyUploadAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<Object> mPhotos;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public DailyUploadAdapter(Context context) {
        this.mContext = context;
    }

    public void setPhotos(List<Object> photos) {
        this.mPhotos = photos;
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        PhotoViewHolder photoViewHolder = new PhotoViewHolder(LayoutInflater.from(this.mContext)
                .inflate(R.layout.item_daily_three, viewGroup, false));

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
        Object photo = mPhotos.get(i);
        if (photo instanceof Uri) {
            photoViewHolder.mPhotoAppCompatImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageManager.loadImage(this.mContext, (Uri) photo, photoViewHolder.mPhotoAppCompatImageView);
        } else if (photo instanceof String) {
            photoViewHolder.mPhotoAppCompatImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageManager.loadImage(this.mContext, (String) photo, photoViewHolder.mPhotoAppCompatImageView);
        } else {
            photoViewHolder.mPhotoAppCompatImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ImageManager.loadImage(this.mContext, (int) photo, photoViewHolder.mPhotoAppCompatImageView);
        }
    }

    @Override
    public int getItemCount() {
        if(mPhotos != null) {
            return mPhotos.size();
        }
        return 0;
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView mPhotoAppCompatImageView;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mPhotoAppCompatImageView = itemView.findViewById(R.id.photo);
        }
    }
}
