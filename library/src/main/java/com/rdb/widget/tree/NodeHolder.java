package com.rdb.widget.tree;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.util.ArrayList;
import java.util.List;

public class NodeHolder implements View.OnClickListener {

    private Node node;
    private int level;
    private int position;
    private View nodeView;
    private Provider provider;
    private NodeHolder parent;
    private LinearLayout nodeLayout;
    private LinearLayout childLayout;
    private OnExpandChangeListener onExpandChangeListener;
    private List<NodeHolder> childNodeHolders = new ArrayList<>();

    private NodeHolder(Context context, Provider provider, Node node) {
        this.node = node;
        this.provider = provider;
        nodeLayout = new LinearLayout(context);
        nodeLayout.setOrientation(LinearLayout.VERTICAL);
        nodeView = provider.inflaterNodeView(nodeLayout, node.getNodeType());
        nodeLayout.addView(nodeView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nodeLayout.setOnClickListener(this);
    }

    void setLevelAndPosition(int level, int position) {
        this.level = level;
        this.position = position;
    }

    public Node getNode() {
        return node;
    }

    private void setNode(Node node) {
        this.node = node;
    }

    public NodeHolder getParent() {
        return parent;
    }

    void setParent(NodeHolder parent) {
        this.parent = parent;
    }

    public int getType() {
        return node.getNodeType();
    }

    public int getLevel() {
        return level;
    }

    public int getPosition() {
        return position;
    }

    public View getNodeView() {
        return nodeView;
    }

    public void setOnExpandChangeListener(OnExpandChangeListener onExpandChangeListener) {
        this.onExpandChangeListener = onExpandChangeListener;
    }

    public NodeHolder getNodeHolder(int rootPos) {
        return childNodeHolders.get(rootPos);
    }

    public LinearLayout getNodeLayout() {
        return nodeLayout;
    }

    public boolean switchExpand() {
        if (node.getChildNodeCount() > 0) {
            boolean expanded = !isExpanded();
            provider.setExpanded(node, expanded);
            if (expanded && childNodeHolders.size() == 0) {
                updateChild();
            }
            if (onExpandChangeListener != null) {
                onExpandChangeListener.onExpandChanged(this, expanded);
            }
            if (childLayout != null) {
                childLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            }
            return true;
        }
        return false;
    }

    public boolean isExpanded() {
        return provider.isExpanded(this);
    }

    public void setExpanded(boolean expanded) {
        if (expanded != isExpanded()) {
            switchExpand();
        }
    }

    public void notifyNodeChanged() {
        if (childLayout != null) {
            childLayout.removeAllViews();
            for (NodeHolder holder : childNodeHolders) {
                holder.recycler();
            }
            childNodeHolders.clear();
        }
        provider.onUpdate(this);
        if (isExpanded()) {
            updateChild();
        }
    }

    private void updateChild() {
        int childCount = node.getChildNodeCount();
        if (childCount > 0) {
            if (childLayout == null) {
                View childContainer = nodeView.findViewById(provider.getChildContainerId(node.getNodeType()));
                if (childContainer != null && childContainer instanceof LinearLayout) {
                    childLayout = (LinearLayout) childContainer;
                }
            }
            if (childLayout == null) {
                childLayout = new LinearLayout(nodeLayout.getContext());
                nodeLayout.addView(childLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            childLayout.setOrientation(LinearLayout.VERTICAL);
        }
        int childLevel = level + 1;
        for (int i = 0; i < childCount; i++) {
            Node childNode = node.getChildNode(i);
            NodeHolder nodeHolder = provider.get(childNode);
            nodeHolder.setParent(this);
            nodeHolder.setLevelAndPosition(childLevel, i);
            childLayout.addView(nodeHolder.nodeLayout);
            childNodeHolders.add(nodeHolder);
            nodeHolder.notifyNodeChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (!provider.onClick(node)) {
            switchExpand();
        }
    }

    void recycler() {
        provider.put(node.getNodeType(), this);
    }

    public interface OnExpandChangeListener {
        void onExpandChanged(NodeHolder nodeHolder, boolean expanded);
    }

    abstract static class Provider {

        private Context context;
        private LayoutInflater layoutInflater;
        private SparseArray<Boolean> expandArray = new SparseArray<>();
        private SparseArray<List<NodeHolder>> cacheItemViews = new SparseArray<>();

        Provider(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        NodeHolder get(Node node) {
            List<NodeHolder> nodeHolders = cacheItemViews.get(node.getNodeType());
            NodeHolder nodeHolder;
            if (nodeHolders != null && nodeHolders.size() > 0) {
                nodeHolder = nodeHolders.remove(0);
                nodeHolder.setNode(node);
            } else {
                nodeHolder = new NodeHolder(context, this, node);
            }
            return nodeHolder;
        }

        void put(int type, NodeHolder nodeHolder) {
            List<NodeHolder> nodeHolders = cacheItemViews.get(type);
            if (nodeHolders == null) {
                nodeHolders = new ArrayList<>();
                cacheItemViews.put(type, nodeHolders);
            }
            nodeHolder.setNode(null);
            nodeHolder.setParent(null);
            nodeHolders.add(nodeHolder);
        }

        private boolean isExpanded(NodeHolder nodeHolder) {
            Boolean expanded = expandArray.get(nodeHolder.node.getNodeId());
            if (expanded == null) {
                expanded = isExpanded(nodeHolder.node, nodeHolder.level, nodeHolder.position);
                expandArray.put(nodeHolder.node.getNodeId(), expanded);
            }
            return expanded;
        }

        private void setExpanded(Node node, boolean expanded) {
            expandArray.put(node.getNodeId(), expanded);
        }

        void clearExpanded() {
            expandArray.clear();
        }

        private View inflaterNodeView(ViewGroup parent, int type) {
            return layoutInflater.inflate(getNodeLayout(type), parent, false);
        }

        public abstract @LayoutRes
        int getNodeLayout(int type);

        public abstract @IdRes
        int getChildContainerId(int type);

        public abstract boolean onClick(Node node);

        public abstract void onUpdate(NodeHolder nodeHolder);

        public abstract boolean isExpanded(Node node, int level, int position);
    }
}