package com.rdb.widget.adapter;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;

public abstract class BaseItemHolder extends ObjectMap {

    private final View itemView;
    private SparseArray<View> views = new SparseArray<>();

    public BaseItemHolder(View itemView) {
        itemView.setTag(this);
        this.itemView = itemView;
    }

    public <T extends View> T findViewById(@IdRes int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getItemView() {
        return itemView;
    }
}
