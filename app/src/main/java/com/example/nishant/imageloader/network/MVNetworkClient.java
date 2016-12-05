package com.example.nishant.imageloader.network;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.example.nishant.imageloader.network.parser.JsonParser;
import com.example.nishant.imageloader.network.parser.Parser;
import com.example.nishant.imageloader.network.pool.CachePoolManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nishant on 11/30/2016.
 * This is a service class, and serves fe specific purpose only
 */

public final class MVNetworkClient {

    private static final int STRING_LOAD_COMPLETE = 1;
    private static final int LOAD_FAILED = 2;

    private static final int DEFAULT_REQUEST_QUEUE_SIZE = 8;
    private static final String TAG = MVNetworkClient.class.getSimpleName();
    private static final int IMAGE_LOAD_COMPLETE = 3;

    private static MVNetworkClient sInstance = new MVNetworkClient();
    private final HashMap<String, AbstractRequest> urlRequestMap = new HashMap<>();
    private final HashMap<String, HashSet<ResponseListener>> registeredListeners = new HashMap<>();
    private final RequestQueue queue;
    private final ExecutorService executor;
    private final Handler handler = new Handler(new HandlerCallback());
    private final CachePoolManager cachePoolManager = CachePoolManager.getInstance();
    private final Parser parser = new JsonParser(); // json type api can be changed to xml based api here

    private MVNetworkClient() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("should be called from main thread");
        }

        this.queue = new RequestQueue(DEFAULT_REQUEST_QUEUE_SIZE);
        this.executor = new ExecutorService(queue);
    }

    public static MVNetworkClient getInstance() {
        return sInstance;
    }

    public void addStringRequest(ResponseListener responseListener) {
        String requestedUrl = responseListener.getUrl();

        /*String fromCache = cachePoolManager.getString(requestedUrl);
        if (fromCache != null) {
            responseListener.onSuccess(fromCache); //awesome, we got a hit
            return;
        }*/

        updateListenersForCurrentRequest(responseListener);
        responseListener.addParser(parser);

        //if there are no current request, place a new request
        if (urlRequestMap.get(responseListener.getUrl()) == null) {

            StringRequest stringRequest = new StringRequest(requestedUrl,
                    new GenericResponseListener<String>(requestedUrl, handler));

            urlRequestMap.put(requestedUrl, stringRequest);
            executor.submit(stringRequest);
        }
    }

    public void addImageRequest(ImageView imageView, ResponseListener<Bitmap> responseListener) {

        if (imageView.getWidth() == 0 && imageView.getHeight() == 0) {
            waitForLayoutChange(imageView, responseListener);
            return;
        }

        String requestedUrl = responseListener.getUrl();
        Bitmap fromCache = cachePoolManager.getBitMap(requestedUrl);
        if (fromCache != null) {
            responseListener.onSuccess(fromCache);
            return;
        }

        responseListener.setId(imageView.hashCode());

        //use hashcode to identify imageview and request made by them
        if (imageView.getTag() == null) {
            imageView.setTag(new ImageRequestTracker(imageView.hashCode(), requestedUrl));
        } else {
            cancelAnyPreviousRequest(imageView, responseListener);
        }

        updateListenersForCurrentRequest(responseListener);

        //if there are no current request for this url, place a new request
        if (urlRequestMap.get(requestedUrl) == null) {
            ImageRequest imageRequest = new ImageRequest(requestedUrl, imageView.getWidth(), imageView.getHeight(),
                    new GenericResponseListener<Bitmap>(requestedUrl, handler));

            urlRequestMap.put(requestedUrl, imageRequest);
            executor.submit(imageRequest);
        }
    }

    private void updateListenersForCurrentRequest(ResponseListener responseListener) {
        HashSet<ResponseListener> allListenerForCurrentReq = registeredListeners.get(responseListener.getUrl());
        if (allListenerForCurrentReq == null) {
            allListenerForCurrentReq = new HashSet<>();
        }
        allListenerForCurrentReq.add(responseListener);
        registeredListeners.put(responseListener.getUrl(), allListenerForCurrentReq);
    }

    private void cancelAnyPreviousRequest(ImageView imageView, ResponseListener responseListener) {

        if (!(imageView.getTag() instanceof ImageRequestTracker)) {
            throw new IllegalStateException("image view object should not have any tag");
        }

        ImageRequestTracker imageRequestTracker = (ImageRequestTracker) imageView.getTag();
        if (!imageRequestTracker.url.equals(responseListener.getUrl())) {
            AbstractRequest request = urlRequestMap.get(imageRequestTracker.url);

            //for all request for this url, remove the previous request made by the same imageview
            HashSet<ResponseListener> allListenersForCurrentReq = registeredListeners.get(imageRequestTracker.url);
            Iterator<ResponseListener> it = allListenersForCurrentReq.iterator();
            //TODO fast ds fore removal in fast scrolling
            while (it.hasNext()) {
                if (it.next().getId() == imageRequestTracker.id) {
                    it.remove();
                }
            }

            //if there are no more listeners for this request, cancel it
            if (allListenersForCurrentReq.size() == 0 && request != null) {
                request.cancel();
                queue.remove(request);
            }

            imageView.setTag(new ImageRequestTracker(imageView.hashCode(), responseListener.getUrl()));
        }
    }

    private void waitForLayoutChange(final ImageView imageView, final ResponseListener responseListener) {
        imageView.addOnLayoutChangeListener(new LayoutChangeListener(imageView, responseListener));
    }

    private static class LayoutChangeListener implements View.OnLayoutChangeListener {

        private final WeakReference<ImageView> weakImageViewRef;
        private final WeakReference<ResponseListener> weakRespListener;

        LayoutChangeListener(ImageView imageView, ResponseListener responseListener) {
            weakImageViewRef = new WeakReference<ImageView>(imageView);
            weakRespListener = new WeakReference<ResponseListener>(responseListener);
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            ImageView imageView = weakImageViewRef.get();
            ResponseListener responseListener = weakRespListener.get();
            if (imageView != null) {
                imageView.removeOnLayoutChangeListener(this);
                if (responseListener != null) {
                    MVNetworkClient.getInstance().addImageRequest(imageView, responseListener);
                }
            }
        }
    }

    void removeRequest(String url) {
        urlRequestMap.remove(url);
    }

    private static class ImageRequestTracker {
        int id;
        String url;

        ImageRequestTracker(int id, String url) {
            this.id = id;
            this.url = url;
        }
    }

    private static class GenericResponseListener<T> implements AbstractRequest.IResourceLoadListener {

        private final String url;
        private final Handler handler;

        GenericResponseListener(String url, Handler handler) {
            this.url = url;
            this.handler = handler;
        }

        @Override
        public void onResourceLoadComplete(Object object) {
            Message message = Message.obtain(handler);
            message.obj = url;
            if (object instanceof Bitmap) {
                CachePoolManager.getInstance().putBitMap(url, (Bitmap) object);
            } else {
                CachePoolManager.getInstance().putString(url, (String) object);
            }
            message.arg1 = object instanceof Bitmap ? IMAGE_LOAD_COMPLETE : STRING_LOAD_COMPLETE;
            handler.sendMessage(message);
        }

        @Override
        public void onLoadFailed(String errorMessage) {
            Message message = Message.obtain(handler);
            message.obj = url;
            message.arg1 = LOAD_FAILED;
            handler.sendMessage(message);
        }
    }

    public void cancel(String url) {
        AbstractRequest request = urlRequestMap.get(url);
        if (request != null) {
            urlRequestMap.remove(url);
            registeredListeners.remove(url);
            request.cancel();
        }
    }

    private class HandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {

            if (msg.arg1 == IMAGE_LOAD_COMPLETE || msg.arg1 == STRING_LOAD_COMPLETE) {
                String url = (String) msg.obj;
                HashSet<ResponseListener> responseListeners = registeredListeners.get(url);
                for (ResponseListener responseListener : responseListeners) {
                    responseListener.onSuccess(msg.arg1 == IMAGE_LOAD_COMPLETE ? CachePoolManager.getInstance().getBitMap(url)
                            : CachePoolManager.getInstance().getString(url));
                }
                responseListeners.clear();
                urlRequestMap.remove(url);
            } else if (msg.arg1 == LOAD_FAILED) {
                String url = (String) msg.obj;
                HashSet<ResponseListener> responseListeners = registeredListeners.get(url);
                for (ResponseListener responseListener : responseListeners) {
                    responseListener.Error("");
                }
                responseListeners.clear();
                urlRequestMap.remove(url);
            }
            return false;
        }
    }

}
