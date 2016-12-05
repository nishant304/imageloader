package com.example.nishant.imageloader.network;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nishant on 11/30/2016.
 */

public abstract class AbstractRequest<T> implements Runnable {

    private static final String TAG = AbstractRequest.class.getSimpleName();
    private final String url;
    protected final Set<IResourceLoadListener> list;
    protected volatile boolean interrupted;

    protected AbstractRequest(String url, IResourceLoadListener resourceLoadListener) {
        this.url = url;
        this.list = new HashSet<>();
        list.add(resourceLoadListener);
    }

    @Override
    public void run() {
        //Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        loadResourceInBackGround(url);
    }

    protected abstract void loadResourceInBackGround(String url);

    public void register(IResourceLoadListener resourceLoadListener) {
        list.add(resourceLoadListener);
    }

    public void unRegister(IResourceLoadListener resourceLoadListener) {
        list.remove(resourceLoadListener);
    }

    public void cancel() {
        interrupted = true;
        notifyFailure("load cancelled on request");
    }

    public interface IResourceLoadListener<T> {
        void onResourceLoadComplete(T t);

        void onLoadFailed(String errorMessage);
    }

    protected void notifySuccess(T t) {
        for (IResourceLoadListener r : list) {
            r.onResourceLoadComplete(t);
        }
        list.clear();
    }

    protected void notifyFailure(String message) {
        for (IResourceLoadListener r : list) {
            r.onLoadFailed(message);
        }
        list.clear();
    }
}
