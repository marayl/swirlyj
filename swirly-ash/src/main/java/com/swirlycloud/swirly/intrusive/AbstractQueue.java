/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.intrusive;

import org.eclipse.jdt.annotation.NonNull;

import com.swirlycloud.swirly.collection.Sequence;

public abstract class AbstractQueue<V> implements Sequence<V> {
    private V first;
    private V last;

    protected abstract void setNext(V node, V next);

    protected abstract V next(V node);

    @Override
    public final void clear() {
        first = null;
        last = null;
    }

    public final void clearTail() {
        assert first != null;
        setNext(first, null);
        last = first;
    }

    @Override
    public final void add(@NonNull V node) {
        insertBack(node);
    }

    public final void insertBack(V node) {
        if (!isEmpty()) {
            setNext(last, node);
        } else {
            first = node;
        }
        last = node;
        setNext(node, null);
    }

    public final V removeFirst() {
        if (isEmpty()) {
            return null;
        }
        final V node = first;
        first = next(first);
        if (isEmpty()) {
            last = null;
        }
        return node;
    }

    public final void join(AbstractQueue<V> rhs) {
        if (!rhs.isEmpty()) {
            if (!isEmpty()) {
                setNext(last, rhs.first);
            } else {
                first = rhs.first;
            }
            last = rhs.last;
        }
    }

    @Override
    public final V getFirst() {
        return first;
    }

    public final V getLast() {
        return last;
    }

    @Override
    public final boolean isEmpty() {
        return first == null;
    }
}
