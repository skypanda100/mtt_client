package gzt.mtt.Adapter;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.youth.banner.loader.ImageLoader;

import gzt.mtt.Constant;

public class FoodGradeAdapter extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Picasso.get().load(Constant.BaseImageUrl + path).into(imageView);
    }
}
