package gzt.mtt.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FoodGradeAdapter extends PagerAdapter {
    private List<String> mImages = new ArrayList<>();
    private Context mContext;

    public FoodGradeAdapter(Context context) {
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
//        ImageView imageView = new ImageView(this.mContext);
        PhotoView photoView = new PhotoView(this.mContext);
        String path = this.mImages.get(position);
        if (path.startsWith("/storage")) {
            Picasso.with(this.mContext).load(new File(path)).into(photoView);
        } else {
            Picasso.with(this.mContext).load(path).into(photoView);
        }
        container.addView(photoView);
        return photoView;
    }
}