package com.rdb.widget.page;

public enum PageIndex {
    PRE_PAGE(-1), CUR_PAGE(0), NEXT_PAGE(1);
    int value;

    PageIndex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
