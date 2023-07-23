package com.rdb.widget.tree;


import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class TreeView extends LinearLayout {

    private TreeAdapter adapter;
    private HolderProvider holderProvider;
    private OnNodeClickListener onNodeClickListener;
    private List<NodeHolder> rootNodeHolders = new ArrayList<>();
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            onDataChanged();
        }
    };

    public TreeView(Context context) {
        super(context);
        initViews(context);
    }

    public TreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        holderProvider = new HolderProvider(context);
    }

    public TreeAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TreeAdapter adapter) {
        if (this.adapter != adapter) {
            if (this.adapter != null) {
                this.adapter.unregisterDataSetObserver(dataSetObserver);
            }
            this.adapter = adapter;
            if (this.adapter != null) {
                this.adapter.registerDataSetObserver(dataSetObserver);
            }
        }
        onDataChanged();
    }

    public void setOnNodeClickListener(OnNodeClickListener onNodeClickListener) {
        this.onNodeClickListener = onNodeClickListener;
    }

    public boolean setRootExpanded(int rootPos, boolean expanded) {
        if (rootPos >= 0 && rootNodeHolders.size() > rootPos) {
            rootNodeHolders.get(rootPos).setExpanded(expanded);
            return true;
        }
        return false;
    }

    public void clearExpanded() {
        holderProvider.clearExpanded();
    }

    public NodeHolder getNodeHolder(int rootPos) {
        return rootNodeHolders.get(rootPos);
    }

    private void onDataChanged() {
        removeAllViews();
        for (NodeHolder holder : rootNodeHolders) {
            holder.recycler();
        }
        rootNodeHolders.clear();
        if (adapter != null) {
            int count = adapter.getRootNodeCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    Node node = adapter.getRootNode(i);
                    NodeHolder nodeHolder = holderProvider.get(node);
                    nodeHolder.setParent(null);
                    nodeHolder.setLevelAndPosition(0, i);
                    addView(nodeHolder.getNodeLayout());
                    rootNodeHolders.add(nodeHolder);
                    nodeHolder.notifyNodeChanged();
                }
            }
        }
    }

    public static abstract class OnNodeClickListener {

        public abstract boolean onNodeClick(Node node);
    }

    public class HolderProvider extends NodeHolder.Provider {

        HolderProvider(Context context) {
            super(context);
        }

        @Override
        public int getNodeLayout(int type) {
            return adapter.getNodeLayout(type);
        }

        @Override
        public int getChildContainerId(int type) {
            return adapter.getChildContainerId(type);
        }

        @Override
        public boolean onClick(Node node) {
            if (onNodeClickListener != null) {
                return onNodeClickListener.onNodeClick(node);
            }
            return false;
        }

        @Override
        public void onUpdate(NodeHolder nodeHolder) {
            int totalCount = nodeHolder.getLevel() == 0 ? adapter.getRootNodeCount() : nodeHolder.getParent().getNode().getChildNodeCount();
            adapter.updateNodeView(nodeHolder, nodeHolder.getNodeView(), nodeHolder.getNode(), nodeHolder.getLevel(), nodeHolder.getPosition(), totalCount);
        }

        @Override
        public boolean isExpanded(Node node, int level, int position) {
            return adapter.isExpanded(node, level, position);
        }
    }
}