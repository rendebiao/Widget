package com.rdb.widget.adapter;

/**
 * Created by DB on 2017/1/17.
 */

public interface Adaptable<T extends ViewAdapter> {
    T getAdapter();

    void setAdapter(T adapter);

    AdapterProxy getAdapterProxy();
}
