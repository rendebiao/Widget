package com.rdb.widget.list;

import com.rdb.widget.Adapter;
import com.rdb.widget.AdapterProxy;

/**
 * Created by DB on 2017/1/17.
 */

public interface Adaptable<T extends Adapter> {
    T getAdapter();

    void setAdapter(T adapter);

    AdapterProxy getAdapterProxy();
}
