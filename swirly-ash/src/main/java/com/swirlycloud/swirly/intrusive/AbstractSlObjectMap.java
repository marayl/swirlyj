/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.intrusive;

import com.swirlycloud.swirly.node.SlNode;

public abstract class AbstractSlObjectMap<K> extends AbstractObjectMap<K, SlNode> {

    @Override
    protected final void setNext(SlNode node, SlNode next) {
        node.setSlNext(next);
    }

    @Override
    protected final SlNode next(SlNode node) {
        return node.slNext();
    }

    public AbstractSlObjectMap(int capacity) {
        super(capacity);
    }
}
