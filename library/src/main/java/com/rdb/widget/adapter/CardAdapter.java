package com.rdb.widget.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by DB on 2017/6/5.
 */

public abstract class CardAdapter extends ViewAdapter {

    public abstract int getCardCount();

    public abstract int getShowCardCount();

    public abstract int getCardHeight();

    public abstract View getView(int position, View convertView, ViewGroup parent);
}