package com.android.powermon.monitor;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 11/17/11
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Monitor {
    void enable();
    void disable();
    String getState();
}
