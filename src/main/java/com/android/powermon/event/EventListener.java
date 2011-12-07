package com.android.powermon.event;

public interface EventListener<T> {
    public void onEvent(T event);
}
