package com.rdb.widget.adapter;

/**
 * Created by DB on 2017/6/5.
 */
public class SingleLayoutProvider<T> implements LayoutProvider<T> {

    private int layoutId;

    public SingleLayoutProvider(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public int getLayout(int type) {
        return layoutId;
    }

    @Override
    public int getLayoutType(int position, T t) {
        return 0;
    }

    @Override
    public int getLayoutCount() {
        return 1;
    }
}
