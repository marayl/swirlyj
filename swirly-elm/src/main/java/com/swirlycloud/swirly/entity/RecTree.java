/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.entity;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.swirly.intrusive.AbstractObjectTree;

/**
 * Record tree keyed by mnemonic. Records are identified by mnemonic only, so instances should not
 * be used as heterogeneous Record containers, where Records of different types may share the same
 * identity.
 *
 * @author Mark Aylett
 */
public final @NonNullByDefault class RecTree extends AbstractObjectTree<String, Rec> {

    private static final long serialVersionUID = 1L;

    @Override
    protected final void setNode(Rec lhs, Rec rhs) {
        lhs.setNode(rhs);
    }

    @Override
    protected final void setLeft(Rec node, @Nullable Rec left) {
        node.setLeft(left);
    }

    @Override
    protected final void setRight(Rec node, @Nullable Rec right) {
        node.setRight(right);
    }

    @Override
    protected final void setParent(Rec node, @Nullable Rec parent) {
        node.setParent(parent);
    }

    @Override
    protected final void setColor(Rec node, int color) {
        node.setColor(color);
    }

    @Override
    protected final @Nullable Rec next(Rec node) {
        return (Rec) node.rbNext();
    }

    @Override
    protected final @Nullable Rec prev(Rec node) {
        return (Rec) node.rbPrev();
    }

    @Override
    protected final @Nullable Rec getLeft(Rec node) {
        return (Rec) node.getLeft();
    }

    @Override
    protected final @Nullable Rec getRight(Rec node) {
        return (Rec) node.getRight();
    }

    @Override
    protected final @Nullable Rec getParent(Rec node) {
        return (Rec) node.getParent();
    }

    @Override
    protected final int getColor(Rec node) {
        return node.getColor();
    }

    @Override
    protected final int compareNode(Rec lhs, Rec rhs) {
        return lhs.getMnem().compareTo(rhs.getMnem());
    }

    @Override
    protected final int compareKey(Rec lhs, String rhs) {
        return lhs.getMnem().compareTo(rhs);
    }
}
