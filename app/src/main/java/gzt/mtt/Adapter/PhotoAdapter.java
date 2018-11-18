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

    public PhotoAdapter(Context context) {
        this.mContext = context;
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