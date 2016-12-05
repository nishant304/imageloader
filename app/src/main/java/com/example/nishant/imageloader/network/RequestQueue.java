package com.example.nishant.imageloader.network;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by nishant on 11/30/2016.
 */

public class RequestQueue extends LinkedBlockingDeque<Runnable> {

    private final int maxCapacity;

    public RequestQueue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean add(Runnable o) {
        addLast(o);
        return true;
    }

    @Override
    public void addLast(Runnable abstractRequest) {
        if (size() >= maxCapacity) {
            removeLast();
        }
        super.addLast(abstractRequest);
    }
}
