package gzt.mtt.Manager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import gzt.mtt.GlideApp;
import gzt.mtt.R;
import gzt.mtt.Util.PathUtil;

public class ImageManager {
    private static CustomViewTarget createCustomViewTarget(final String tag, final ImageView imageView) {
        // set tag
        imageView.setTag(tag);

        CustomViewTarget customViewTarget = new CustomViewTarget(imageView) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {
                Log.d("zdt", "clear");
            }

            @Override
            protected void onResourceLoading(@Nullable Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                imageView.setImageDrawable(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
                if(tag.equals(imageView.getTag())) {
                    imageView.setImageDrawable((Drawable) resource);
                }
            }
        };

        return customViewTarget;
    }

    public static void loadImage(Context context, final String url, final ImageView imageView) {
        // target
        CustomViewTarget customViewTarget = createCustomViewTarget(url, imageView);
        // load
        GlideApp.with(context).load(url).placeholder(R.drawable.loading).error(R.drawable.loading).thumbnail(0.2f).into(customViewTarget);
    }

    public static void loadImage(Context context, final Uri uri, final ImageView imageView) {
        // target
        String path = PathUtil.uri2path(context, uri);
        CustomViewTarget customViewTarget = createCustomViewTarget(path, imageView);
        // load
        GlideApp.with(context).load(uri).placeholder(R.drawable.loading).error(R.drawable.loading).thumbnail(0.2f).into(customViewTarget);
    }

    public static void loadImage(Context context, final File file, final ImageView imageView) {
        // target
        String path = file.getAbsolutePath();
        CustomViewTarget customViewTarget = createCustomViewTarget(path, imageView);
        // load
        GlideApp.with(context).load(file).placeholder(R.drawable.loading).error(R.drawable.loading).thumbnail(0.2f).into(customViewTarget);
    }

    public static void loadImage(Context context, final int id, final ImageView imageView) {
        // target
        String path = String.valueOf(id);
        CustomViewTarget customViewTarget = createCustomViewTarget(path, imageView);
        // load
        GlideApp.with(context).load(id).placeholder(R.drawable.loading).error(R.drawable.loading).thumbnail(0.2f).into(customViewTarget);
    }

    public static void clearView(Context context, final ImageView imageView) {
        GlideApp.with(context).clear(imageView);
    }
}
