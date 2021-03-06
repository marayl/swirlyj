/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.intrusive;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import com.swirlycloud.swirly.node.AbstractDlNode;
import com.swirlycloud.swirly.node.DlNode;

public final @NonNullByDefault class DlList extends AbstractList<DlNode> {

    @Override
    protected final void insert(DlNode node, DlNode prev, DlNode next) {
        node.insert(prev, next);
    }

    @Override
    protected final void insertBefore(DlNode node, DlNode next) {
        node.insertBefore(next);
    }

    @Override
    protected final void insertAfter(DlNode node, DlNode prev) {
        node.insertAfter(prev);
    }

    @Override
    protected final void remove(DlNode node) {
        node.remove();
    }

    @Override
    protected final void setPrev(DlNode node, @NonNull DlNode prev) {
        node.setDlPrev(prev);
    }

    @Override
    protected final void setNext(DlNode node, @NonNull DlNode next) {
        node.setDlNext(next);
    }

    @Override
    protected final DlNode prev(DlNode node) {
        return node.dlPrev();
    }

    @Override
    protected final DlNode next(DlNode node) {
        return node.dlNext();
    }

    public DlList() {
        super(new AbstractDlNode() {
            @Override
            public final boolean isEnd() {
                return true;
            }
        });
    }
}
