package com.rdb.widget.adapter;

import android.database.DataSetObserver;

import java.util.HashSet;

/**
 * Created by DB on 2017/1/16.
 */

public abstract class ViewAdapter {

    private HashSet<DataSetObserver> mObservers = new HashSet<>();

    public void registerDataSetObserver(DataSetObserver observer) {
        mObservers.add(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservers.remove(observer);
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver observer : mObservers) {
            observer.onChanged();
        }
    }
}
