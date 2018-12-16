package gzt.mtt.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gzt.mtt.Manager.ImageManager;


public class PhotoAdapter extends PagerAdapter {
    private List<String> mImages = new ArrayList<>();
    private Context mContext;
    private OnImageClickListener mOnImageClickListener;
    private OnImageLongClickListener mOnImageLongClickListener;

    public interface OnImageClickListener{
        void onImageClick(View view);
    }

    public interface OnImageLongClickListener{
        void onImageLongClick(View view);
    }

    public PhotoAdapter(Context context) {
        this.mContext = context;
    }

    public void setImageClickListener (OnImageClickListener onImageClickListener) {
        this.mOnImageClickListener = onImageClickListener;
    }

    public void setImageLongClickListener (OnImageLongClickListener onImageLongClickListener) {
        this.mOnImageLongClickListener = onImageLongClickListener;
    }

    public void setImages(List<String> images) {
        this.mImages = images;
    }

    @Override
    public int getCount() {
        return this.mImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        PhotoView photoView = new PhotoView(this.mContext);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageClick(v);
                }
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnImageLongClickListener != null) {
                    mOnImageLongClickListener.onImageLongClick(v);
                }
                return false;
            }
        });
        String path = this.mImages.get(position);
        if (path.startsWith("/storage")) {
            ImageManager.loadImage(this.mContext, new File(path), photoView);
        } else {
            ImageManager.loadImage(this.mContext, path, photoView);
        }
        container.addView(photoView);
        return photoView;
    }
}