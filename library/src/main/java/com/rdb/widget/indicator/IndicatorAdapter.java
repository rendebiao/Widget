package com.rdb.widget.indicator;

import com.rdb.widget.Adapter;

public abstract class IndicatorAdapter extends Adapter {

    public abstract int getCount();

    public abstract int getSelect();

    public abstract float getOffset();
}
