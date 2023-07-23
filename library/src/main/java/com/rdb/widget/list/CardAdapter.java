package com.rdb.widget.list;

import android.view.View;
import android.view.ViewGroup;

import com.rdb.widget.Adapter;

/**
 * Created by DB on 2017/6/5.
 */

public abstract class CardAdapter extends Adapter {

    public abstract int getCardCount();

    public abstract int getShowCardCount();

    public abstract int getCardHeight();

    public abstract View getView(int position, View convertView, ViewGroup parent);
}