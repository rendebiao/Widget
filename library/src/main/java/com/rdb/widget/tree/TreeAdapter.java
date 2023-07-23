package com.rdb.widget.tree;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

public abstract class TreeAdapter {

    private DataSetObservable dataSetObservable = new DataSetObservable();

    void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        dataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        dataSetObservable.notifyInvalidated();
    }

    public abstract int getRootNodeCount();

    public abstract Node getRootNode(int position);

    public abstract @LayoutRes
    int getNodeLayout(int type);

    public @IdRes
    int getChildContainerId(int type) {
        return -1;
    }

    public abstract void updateNodeView(NodeHolder nodeHolder, View nodeView, Node node, int level, int position, int count);

    public boolean isExpanded(Node node, int level, int position) {
        return false;
    }
}
