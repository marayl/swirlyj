/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.io;

import static com.swirlycloud.twirly.node.SlUtil.popNext;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.swirlycloud.twirly.domain.BookFactory;
import com.swirlycloud.twirly.domain.Factory;
import com.swirlycloud.twirly.domain.MarketBook;
import com.swirlycloud.twirly.domain.Order;
import com.swirlycloud.twirly.intrusive.MarketViewTree;
import com.swirlycloud.twirly.intrusive.RecTree;
import com.swirlycloud.twirly.node.RbNode;
import com.swirlycloud.twirly.node.SlNode;

public final @NonNullByDefault class ModelUtil {

    private ModelUtil() {
    }

    public static MarketViewTree readView(Model model, Factory factory)
            throws InterruptedException {

        final MarketViewTree views = new MarketViewTree();

        final RecTree markets = model.readMarket(new BookFactory());
        assert markets != null;

        for (RbNode node = markets.getFirst(); node != null; node = node.rbNext()) {
            final MarketBook book = (MarketBook) node;
            views.insert(book.getView());
        }

        final SlNode firstOrder = model.readOrder(factory);
        for (SlNode node = firstOrder; node != null;) {
            final Order order = (Order) node;
            node = popNext(node);

            if (!order.isDone()) {
                final MarketBook book = (MarketBook) markets.find(order.getMarket());
                if (book != null) {
                    book.insertOrder(order);
                }
            }
        }

        for (RbNode node = markets.getFirst(); node != null; node = node.rbNext()) {
            final MarketBook book = (MarketBook) node;
            book.updateView();
        }

        return views;
    }
}
