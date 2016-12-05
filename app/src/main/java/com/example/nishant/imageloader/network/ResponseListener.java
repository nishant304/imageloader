package com.example.nishant.imageloader.network;

import com.example.nishant.imageloader.network.parser.Parser;

import java.util.List;

/**
 * Created by nishant on 12/4/2016.
 */

public abstract class ResponseListener<T> {
    private final Class<T> tClass;
    private final String url;
    private int id;
    private Parser parser;

    public ResponseListener(Class<T> tClass, String url) {
        this.tClass = tClass;
        this.url = url;
    }

    void onSuccess(T t) {
        if (t instanceof String) {
            onResponse(parser.toList((String) t, tClass));
        } else {
            onResponse(t);
        }
        MVNetworkClient.getInstance().removeRequest(url);
    }

    void Error(String msg) {
        onError(msg);
        MVNetworkClient.getInstance().removeRequest(url);
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public abstract void onResponse(List<T> t);

    public abstract void onResponse(T t);

    public abstract void onError(String message);

    public void addParser(Parser parser) {
        this.parser = parser;
    }
}