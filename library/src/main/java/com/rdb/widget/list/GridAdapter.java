package com.rdb.widget.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdb.widget.Adapter;
import com.rdb.widget.adapter.LayoutProvider;

import java.util.List;

/**
 * Created by DB on 2017/6/5.
 */

public abstract class GridAdapter<T> extends Adapter {

    private int layoutId;
    private List<T> list;
    private Context context;
    private LayoutInflater inflater;
    private LayoutProvider<T> layoutProvider;

    public GridAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.layoutId = layoutId;
        this.inflater = LayoutInflater.from(context);
    }

    public GridAdapter(Context context, List<T> list, LayoutProvider<T> layoutProvider) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.layoutProvider = layoutProvider;
    }

    public final int getCount() {
        return list == null ? 0 : list.size();
    }

    public final T getItem(int position) {
        return list.get(position);
    }

    public final long getItemId(int position) {
        return position;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        BaseItemHolder itemHolder;
        if (convertView == null) {
            convertView = inflater.inflate(layoutProvider == null ? layoutId : layoutProvider.getLayout(layoutProvider.getLayoutType(position, getItem(position))), null);
            itemHolder = new BaseItemHolder(convertView);
        } else {
            itemHolder = (BaseItemHolder) convertView.getTag();
        }
        itemHolder.updateCount++;
        itemHolder.setLastPosition(position);
        updateView(position, itemHolder, getItem(position), parent);
        return convertView;
    }

    public abstract void updateView(int position, BaseItemHolder itemHolder, T data, ViewGroup parent);

    public void notifyDataSetChanged(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public synchronized List<T> getData() {
        return list;
    }

    public BaseItemHolder getItemHolder(View view) {
        if (view != null && view.getTag() != null && view.getTag() instanceof BaseItemHolder) {
            return (BaseItemHolder) view.getTag();
        }
        return null;
    }

    public int getViewTypeCount() {
        return layoutProvider == null ? 1 : layoutProvider.getLayoutCount();
    }

    public int getItemViewType(int position) {
        return layoutProvider == null ? 0 : layoutProvider.getLayoutType(position, getItem(position));
    }

    public List<T> getList() {
        return list;
    }

    public Context getAdapterContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public static class BaseItemHolder extends com.rdb.widget.adapter.BaseItemHolder {

        private int updateCount;
        private int lastPosition;

        public BaseItemHolder(View itemView) {
            super(itemView);
        }

        public int getUpdateCount() {
            return updateCount;
        }

        public int getLastPosition() {
            return lastPosition;
        }

        protected void setLastPosition(int lastPosition) {
            this.lastPosition = lastPosition;
        }
    }
}
