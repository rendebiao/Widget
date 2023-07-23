package com.rdb.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private List<T> list;
    private Context context;
    private LayoutInflater inflater;
    private LayoutProvider<T> layoutProvider;


    public BaseAdapter(Context context, int layoutId, T... t) {
        this.context = context;
        this.list = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            list.add(t[i]);
        }
        this.inflater = LayoutInflater.from(context);
        this.layoutProvider = new SingleLayoutProvider<>(layoutId);
    }

    public BaseAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.layoutProvider = new SingleLayoutProvider<>(layoutId);
    }

    public BaseAdapter(Context context, List<T> list, LayoutProvider<T> layoutProvider) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.layoutProvider = layoutProvider;
    }

    @Override
    public final int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public final T getItem(int position) {
        return list.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            int type = layoutProvider.getLayoutType(position, getItem(position));
            convertView = inflateItem(inflater, parent, layoutProvider.getLayout(type));
            itemHolder = new ItemHolder(type, convertView);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.updateCount++;
        itemHolder.setLastPosition(position);
        updateView(position, itemHolder, getItem(position), parent);
        return convertView;
    }

    protected View inflateItem(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    public abstract void updateView(int position, ItemHolder itemHolder, T data, ViewGroup parent);

    public void notifyDataSetChanged(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public synchronized List<T> getData() {
        return list;
    }

    public ItemHolder getItemHolder(View view) {
        if (view != null && view.getTag() != null && view.getTag() instanceof BaseAdapter.ItemHolder) {
            return (ItemHolder) view.getTag();
        }
        return null;
    }

    @Override
    public final int getViewTypeCount() {
        return layoutProvider == null ? 1 : layoutProvider.getLayoutCount();
    }

    @Override
    public final int getItemViewType(int position) {
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

    public class ItemHolder extends BaseItemHolder {

        private int type;
        private int updateCount;
        private int lastPosition;

        public ItemHolder(int type, View itemView) {
            super(itemView);
            this.type = type;
        }

        public int getType() {
            return type;
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
