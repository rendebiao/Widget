package com.rdb.widget.adapter;

import androidx.annotation.LayoutRes;

/**
 * Created by DB on 2017/6/5.
 */
public interface LayoutProvider<T> {

    @LayoutRes
    int getLayout(int type);

    int getLayoutType(int position, T t);

    int getLayoutCount();
}
