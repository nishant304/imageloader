package com.example.nishant.imageloader.network.pool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nishant on 11/19/2016.
 * A generic url to Object cache
 */

public class CachePoolManager {

    private static final String TAG = CachePoolManager.class.getSimpleName();
    private final MVLruCache mvLruCache;
    private static CachePoolManager cacheManager;
    private final HashSet<SoftReference<Bitmap>> mReusableBitmaps;
    private final StringLruCache stringCache;

    private CachePoolManager() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mvLruCache = new MVLruCache(cacheSize);
        stringCache = new StringLruCache(cacheSize / 3);
        mReusableBitmaps = new HashSet<>();
    }

    /***
     * note that double lock checking is necessary as it is being used in multithreaded environment
     */
    public static CachePoolManager getInstance() {
        if (cacheManager == null) {
            synchronized (CachePoolManager.class) {
                if (cacheManager == null) {
                    cacheManager = new CachePoolManager();
                }
            }
        }
        return cacheManager;
    }

    public Bitmap getBitMap(String url) {
        return mvLruCache.get(url);
    }

    public String getString(String url) {
        return stringCache.get(url);
    }

    public Object get(String url) {
        Object ret = stringCache.get(url);
        if (ret == null) {
            return mvLruCache.get(url);
        }
        return ret;
    }

    public synchronized void put(String url, Object object) {
        if (object == null || url == null) {
            return;
        }

        if ((object instanceof Bitmap) && mvLruCache.get(url) == null) {
            mvLruCache.put(url, (Bitmap) object);
        }

        if ((object instanceof String) && stringCache.get(url) == null) {
            stringCache.put(url, (String) object);
        }

    }

    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bitmap = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = mReusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();
                    if (null != item && item.isMutable()) {
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }

        return bitmap;
    }

    private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (Build.VERSION.SDK_INT < 19) {
            return candidate.getWidth() == targetOptions.outWidth
                    && candidate.getHeight() == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1;
        }

        int width = targetOptions.outWidth / targetOptions.inSampleSize;
        int height = targetOptions.outHeight / targetOptions.inSampleSize;
        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
        return byteCount <= candidate.getAllocationByteCount();
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    public void clearCache() {
        mvLruCache.evictAll();
    }

    public void putBitMap(String url, Bitmap object) {
        mvLruCache.put(url, object);
    }

    public void putString(String url, String object) {
        stringCache.put(url, object);
    }

    private class MVLruCache extends LruCache<String, Bitmap> {

        MVLruCache(int maxSize) {
            super(maxSize);
        }

        //TODO override sizeof


        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            mReusableBitmaps.add(new SoftReference<>((Bitmap) oldValue));

            Log.d(TAG, "entry evicted");
        }
    }

    private class StringLruCache extends LruCache<String, String> {

        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public StringLruCache(int maxSize) {
            super(maxSize);
        }
    }

}
