package com.rdb.widget.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerItemHolder> {

    private List<T> list;
    private Context context;
    private LayoutInflater inflater;
    private LayoutProvider<T> layoutProvider;
    private OnItemClickListener<T> onItemClickListener;
    private OnItemLongClickListener<T> onItemLongClickListener;

    public BaseRecyclerAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.layoutProvider = new SingleLayoutProvider<>(layoutId);
    }

    public BaseRecyclerAdapter(Context context, List<T> list, LayoutProvider<T> layoutProvider) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.layoutProvider = layoutProvider;
    }

    @Override
    public final int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public final RecyclerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflateItem(inflater, parent, layoutProvider.getLayout(viewType));
        return new RecyclerItemHolder(view, viewType);
    }

    protected View inflateItem(LayoutInflater inflater, ViewGroup parent, int layoutId) {
        return inflater.inflate(layoutId, parent, false);
    }

    @Override
    public final void onBindViewHolder(BaseRecyclerAdapter.RecyclerItemHolder itemHolder, int position) {
        T data = getItem(position);
        itemHolder.updateItem(position);
        updateView(position, itemHolder, data);
    }

    public abstract void updateView(int position, RecyclerItemHolder itemHolder, T data);

    public Context getAdapterContext() {
        return context;
    }

    @Override
    public final int getItemViewType(int position) {
        T data = list.get(position);
        return layoutProvider.getLayoutType(position, data);
    }

    private boolean isItemClickEnable() {
        return onItemClickListener != null;
    }

    private boolean isItemLongClickEnable() {
        return onItemLongClickListener != null;
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View view, int position, T data);
    }

    public class RecyclerItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private int position;
        private int viewType;
        private SparseArray<View> views = new SparseArray<>();
        private Map<String, Object> values = new HashMap<>();

        private RecyclerItemHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            itemView.setTag(this);
        }

        public <V extends View> V findViewById(@IdRes int viewId) {
            View view = views.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                views.put(viewId, view);
            }
            return (V) view;
        }

        public Object getValue(String key) {
            return values.get(key);
        }

        public void setValue(String key, Object object) {
            values.put(key, object);
        }

        public int getViewType() {
            return viewType;
        }

        private void updateItem(int position) {
            this.position = position;
            itemView.setOnClickListener(isItemClickEnable() ? this : null);
            itemView.setOnLongClickListener(isItemLongClickEnable() ? this : null);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, position, getItem(position));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(v, position, getItem(position));
                return true;
            }
            return false;
        }
    }
}
