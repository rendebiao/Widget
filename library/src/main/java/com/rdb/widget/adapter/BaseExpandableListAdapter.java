package com.rdb.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseExpandableListAdapter<T, V> extends android.widget.BaseExpandableListAdapter {

    private List<T> list;
    private Context context;
    private int groupLayoutId;
    private int childLayoutId;
    private LayoutInflater inflater;

    public BaseExpandableListAdapter(Context context, List<T> list, int groupLayoutId, int childLayoutId) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.groupLayoutId = groupLayoutId;
        this.childLayoutId = childLayoutId;
    }

    @Override
    public final int getGroupCount() {
        return list.size();
    }

    @Override
    public final T getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    public abstract V getChild(T group, int childPosition);

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public final View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        if (convertView == null) {
            convertView = inflateGroup(inflater, parent, groupLayoutId);
            itemHolder = new ItemHolder(convertView);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.setLastPosition(groupPosition);
        updateGroupView(groupPosition, itemHolder, getGroup(groupPosition), parent);
        return convertView;
    }

    protected View inflateGroup(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public final View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildItemHolder itemHolder;
        if (convertView == null) {
            convertView = inflateGroup(inflater, parent, childLayoutId);
            itemHolder = new ChildItemHolder(convertView);
        } else {
            itemHolder = (ChildItemHolder) convertView.getTag();
        }
        itemHolder.setLastPosition(groupPosition, childPosition);
        updateChildView(groupPosition, childPosition, itemHolder, getChild(getGroup(groupPosition), childPosition), parent);
        return convertView;
    }

    protected View inflateChild(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    public abstract void updateGroupView(int groupPosition, ItemHolder itemHolder, T data, ViewGroup parent);

    public abstract void updateChildView(int groupPosition, int childPosition, ChildItemHolder itemHolder, V data, ViewGroup parent);

    public void notifyDataSetChanged(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ItemHolder getGroupItemHolder(View view) {
        if (view != null && view.getTag() != null && view.getTag() instanceof BaseExpandableListAdapter.ItemHolder) {
            return (ItemHolder) view.getTag();
        }
        return null;
    }


    public ChildItemHolder getChildItemHolder(View view) {
        if (view != null && view.getTag() != null && view.getTag() instanceof BaseExpandableListAdapter.ChildItemHolder) {
            return (ChildItemHolder) view.getTag();
        }
        return null;
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

        private int lastPosition;

        public ItemHolder(View itemView) {
            super(itemView);
        }

        public int getLastPosition() {
            return lastPosition;
        }

        protected void setLastPosition(int lastPosition) {
            this.lastPosition = lastPosition;
        }
    }

    public class ChildItemHolder extends BaseItemHolder {

        private int lastGroupPosition;
        private int lastChildPosition;

        public ChildItemHolder(View itemView) {
            super(itemView);
        }

        protected void setLastPosition(int lastGroupPosition, int lastChildPosition) {
            this.lastGroupPosition = lastGroupPosition;
            this.lastChildPosition = lastChildPosition;
        }

        public int getLastGroupPosition() {
            return lastGroupPosition;
        }

        public int getLastChildPosition() {
            return lastChildPosition;
        }
    }

}
