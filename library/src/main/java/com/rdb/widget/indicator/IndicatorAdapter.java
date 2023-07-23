package com.rdb.widget.indicator;

import com.rdb.widget.adapter.ViewAdapter;

public abstract class IndicatorAdapter extends ViewAdapter {

    public abstract int getCount();

    public abstract int getSelect();

    public abstract float getOffset();
}
