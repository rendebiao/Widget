package com.rdb.widget.tree;

public interface Node {
    int getNodeId();

    int getNodeType();

    int getChildNodeCount();

    Node getChildNode(int position);

    Object getNodeData();
}