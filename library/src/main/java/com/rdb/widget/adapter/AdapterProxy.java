package com.rdb.widget.adapter;

import android.database.DataSetObserver;

/**
 * Created by DB on 2017/1/17.
 */

public abstract class AdapterProxy<T extends ViewAdapter> implements Adaptable<T> {

    private T adapter;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            onDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    @Override
    public T getAdapter() {
        return adapter;
    }

    @Override
    public void setAdapter(T adapter) {
        if (this.adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(dataSetObserver);
            onDataSetChanged();
        }
    }

    protected abstract void onDataSetChanged();

    @Override
    public final AdapterProxy getAdapterProxy() {
        return null;
    }
}
