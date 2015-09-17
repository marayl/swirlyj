/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.app;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.twirly.domain.Exec;
import com.swirlycloud.twirly.domain.MarketBook;
import com.swirlycloud.twirly.domain.Order;
import com.swirlycloud.twirly.domain.Posn;
import com.swirlycloud.twirly.intrusive.SlQueue;
import com.swirlycloud.twirly.node.JslNode;
import com.swirlycloud.twirly.node.SlNode;
import com.swirlycloud.twirly.util.JsonUtil;
import com.swirlycloud.twirly.util.Jsonifiable;
import com.swirlycloud.twirly.util.Params;

public final class Trans implements AutoCloseable, Jsonifiable {
    private String trader;
    private MarketBook book;
    final SlQueue orders = new SlQueue();
    final SlQueue matches = new SlQueue();
    /**
     * All executions referenced in matches.
     */
    final SlQueue execs = new SlQueue();
    /**
     * Optional taker position.
     */
    Posn posn;

    final void reset(@NonNull String trader, @NonNull MarketBook book, @NonNull Order order,
            @NonNull Exec exec) {
        this.trader = trader;
        this.book = book;
        clear();
        orders.insertBack(order);
        execs.insertBack(exec);
    }

    /**
     * Prepare execs by cloning the slNode list from the transNode list.
     * 
     * @return the cloned slNode list.
     */
    final JslNode prepareExecList() {
        final Exec first = (Exec) execs.getFirst();
        Exec node = first;
        while (node != null) {
            Exec next = (Exec) node.slNext();
            node.setJslNext(next);
            node = next;
        }
        return first;
    }

    @Override
    public final String toString() {
        return JsonUtil.toJson(this);
    }

    @Override
    public final void close() {
        clear();
    }

    @Override
    public final void toJson(@Nullable Params params, @NonNull Appendable out) throws IOException {
        out.append("{\"view\":");
        book.toJsonView(params, out);
        // Multiple orders may be updated if one trades with one's self.
        out.append(",\"orders\":[");
        for (SlNode node = orders.getFirst(); node != null; node = node.slNext()) {
            final Order order = (Order) node;
            order.toJson(params, out);
        }
        for (SlNode node = matches.getFirst(); node != null; node = node.slNext()) {
            final Match match = (Match) node;
            if (!match.makerOrder.getTrader().equals(trader)) {
                continue;
            }
            out.append(',');
            match.makerOrder.toJson(params, out);
        }
        out.append("],\"execs\":[");
        int i = 0;
        for (SlNode node = execs.getFirst(); node != null; node = node.slNext()) {
            final Exec exec = (Exec) node;
            if (!exec.getTrader().equals(trader)) {
                continue;
            }
            if (i > 0) {
                out.append(',');
            }
            exec.toJson(params, out);
            ++i;
        }
        out.append("],\"posn\":");
        if (posn != null) {
            posn.toJson(params, out);
        } else {
            out.append("null");
        }
        out.append('}');
    }

    public final void clear() {
        orders.clearAll();
        matches.clear();
        execs.clearAll();
        posn = null;
    }

    public final SlNode getFirstOrder() {
        return orders.getFirst();
    }

    public final SlNode getFirstExec() {
        return execs.getFirst();
    }

    public final boolean isEmptyOrder() {
        return orders.isEmpty();
    }

    public final boolean isEmptyExec() {
        return execs.isEmpty();
    }

    public final Posn getPosn() {
        return posn;
    }
}
