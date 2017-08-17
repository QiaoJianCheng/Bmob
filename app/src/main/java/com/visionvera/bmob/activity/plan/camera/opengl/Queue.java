package com.visionvera.bmob.activity.plan.camera.opengl;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue<T> extends LinkedBlockingQueue<T> {
    private static final long serialVersionUID = -5453583501475139849L;
    private final Object _lockMode1 = new Object();
    private final Object _lockMode2 = new Object();

    public Queue() {

    }

    public Queue(T[] source) {
        for (T t : source) {
            this.offer(t);
        }
    }

    public Queue(List<T> source) {
        for (T t : source) {
            this.offer(t);
        }
    }

    @Override
    public boolean add(T t) {
        synchronized (_lockMode1) {
            return super.add(t);
        }
    }

    @Override
    public T remove() {
        synchronized (_lockMode1) {
            return super.remove();
        }
    }

    @Override
    public boolean offer(T t) {
        synchronized (_lockMode2) {
            return super.offer(t);
        }
    }

    @Override
    public T poll() {
        synchronized (_lockMode2) {
            return super.poll();
        }
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] ts) {
        synchronized (_lockMode1) {
            synchronized (_lockMode2) {
                return super.toArray(ts);
            }
        }
    }
}
