package ch.epfl.culturequest.storage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;

import ch.epfl.culturequest.R;

public class ImageFetcher {

    @FunctionalInterface
    public interface ImageFetcherCallback {
        void onImageReady();
    }

    public static void fetchImage(Context context, String url, ImageView holder, float thumbnailSize, int placeholder) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .thumbnail(Glide.with(context)
                        .load(url)
                        .sizeMultiplier(thumbnailSize))
                .into(holder);
    }

    public static void fetchImage(Fragment context, String url, ImageView holder, float thumbnailSize, int placeholder) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .thumbnail(Glide.with(context)
                        .load(url)
                        .sizeMultiplier(thumbnailSize))
                .into(holder);
    }

    public static void fetchImage(Fragment context, String url, ImageView holder, float thumbnailSize, int placeholder, ImageFetcherCallback callback) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .thumbnail(Glide.with(context)
                        .load(url)
                        .sizeMultiplier(thumbnailSize))
                .into(new ImageViewTarget<Drawable>(holder) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        callback.onImageReady();
                        holder.setImageDrawable(resource);
                    }
                });
    }

    public static void fetchImage(Context context, String url, ImageView holder, float thumbnailSize, int placeholder, ImageFetcherCallback callback) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholder)
                .thumbnail(Glide.with(context)
                        .load(url)
                        .sizeMultiplier(thumbnailSize))
                .into(new ImageViewTarget<Drawable>(holder) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        callback.onImageReady();
                        holder.setImageDrawable(resource);
                    }
                });
    }

    public static void fetchImage(Context context, String url, ImageView holder, int placeholder) {
        fetchImage(context, url, holder, 0.1f, placeholder);
    }

    public static void fetchImage(Fragment context, String url, ImageView holder, int placeholder) {
        fetchImage(context, url, holder, 0.1f, placeholder);
    }

    public static void fetchImage(Context context, String url, ImageView holder, float thumbnailSize) {
        fetchImage(context, url, holder, thumbnailSize, R.drawable.placeholder_image);
    }

    public static void fetchImage(Fragment context, String url, ImageView holder, float thumbnailSize) {
        fetchImage(context, url, holder, thumbnailSize, R.drawable.placeholder_image);
    }



    public static void fetchImage(Context context, String url, ImageView holder) {
        fetchImage(context, url, holder, 0.1f);
    }

    public static void fetchImage(Fragment context, String url, ImageView holder) {
        fetchImage(context, url, holder, 0.1f);
    }

    public static void fetchImage(Context context, String url, ImageView holder, ImageFetcherCallback callback) {
        fetchImage(context, url, holder, 0.1f, R.drawable.placeholder_image, callback);
    }

    public static void fetchImage(Fragment context, String url, ImageView holder, ImageFetcherCallback callback) {
        fetchImage(context, url, holder, 0.1f, R.drawable.placeholder_image, callback);
    }

    public static void preloadImage(Context context, String url) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

}
