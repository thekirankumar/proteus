package com.flipkart.android.proteus.toolbox;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import com.flipkart.android.proteus.ImageLoaderCallback;
import com.google.gson.JsonObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Useful for asynchronous/synchronous loading of network images.
 * This is different in a way that this helper can be used for any view.
 * All you have to do is implement the callback to set any property which accepts drawable as a param.
 * Pass the drawable callback to get the loaded drawable.
 */
public class NetworkDrawableHelper {

    private final View view;
    private final DrawableCallback callback;
    private final BitmapLoader bitmapLoader;
    private final JsonObject layout;

    /**
     * @param context         Android Context
     * @param view            The view which is going to show the drawable. This view will be used to start and stop image loading when view is added and removed from window. Only used when <code>loadImmediately</code> is set to false. Will also work if view is set to null.
     * @param url             The url to load
     * @param loadImmediately Set this to true to load the image on the calling thread (synchronous). If false, volley's thread will be used.
     * @param callback        Implement this to get a hold of the loaded bitmap or the error reason.
     * @param layout
     */
    public NetworkDrawableHelper(final View view, final String url, boolean loadImmediately, DrawableCallback callback,
                                 BitmapLoader bitmapLoader, JsonObject layout) {
        this.view = view;
        this.callback = callback;
        this.bitmapLoader = bitmapLoader;
        this.layout = layout;
        init(url, loadImmediately);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void init(final String url, boolean loadImmediately) {

        // CountDownLatch is used to support loadImmediately param.
        // Since ImageLoader doesnt support synchronous loading, we use CountDownLatch to simulate it.
        final CountDownLatch lock;

        if (view == null) {
            loadImmediately = true;
        }

        if (loadImmediately) {
            startSyncLoad(url, view);
        } else {
            lock = null;
            startAsyncLoad(url, view);
        }
    }

    /**
     * Warning : This method will only work if called from non main thread.
     *
     * @param url
     */
    private void startSyncLoad(String url, View view) {
        Future<Bitmap> future = bitmapLoader.getBitmap(url, view);
        try {
            Bitmap bitmap = future.get(10, TimeUnit.SECONDS);
            callback.onDrawableLoad(url, convertBitmapToDrawable(bitmap, view));
        } catch (Exception e) {
            e.printStackTrace();
            callback.onDrawableError(url, e.getLocalizedMessage(), null);
        }
    }

    private Drawable convertBitmapToDrawable(Bitmap bitmapOriginal, View view) {

        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int width = bitmapOriginal.getWidth();
        int height = bitmapOriginal.getHeight();

        float scaleWidth = displayMetrics.scaledDensity;
        float scaleHeight = displayMetrics.scaledDensity;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);

        return new BitmapDrawable(view.getContext().getResources(), resizedBitmap);
    }

    private void startAsyncLoad(final String url, final View view) {

        bitmapLoader.getBitmap(url, new ImageLoaderCallback() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap == null) return;
                if (callback != null) {
                    callback.onDrawableLoad(url, convertBitmapToDrawable(bitmap, view));
                }
            }

            @Override
            public void onErrorReceived(String errorMessage, Drawable errorDrawable) {
                if (callback != null) {
                    callback.onDrawableError(url, errorMessage, errorDrawable);
                }

            }
        }, view, layout);
    }

    public interface DrawableCallback {
        void onDrawableLoad(String url, Drawable drawable);

        void onDrawableError(String url, String reason, Drawable errorDrawable);
    }
}
