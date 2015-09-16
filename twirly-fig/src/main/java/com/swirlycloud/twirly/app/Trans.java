/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.app;

import static com.swirlycloud.twirly.node.SlUtil.popNext;

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
    private MarketBook book;
    private Order order;
    final SlQueue matches = new SlQueue();
    /**
     * All executions referenced in matches.
     */
    final SlQueue execs = new SlQueue();
    /**
     * Optional taker position.
     */
    Posn posn;

    final void reset(MarketBook book, Order order, Exec exec) {
        assert book != null;
        assert order != null;
        assert exec != null;
        this.book = book;
        this.order = order;
        clear();
        execs.insertBack(exec);
        posn = null;
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
        final String trader = order.getTrader();
        out.append("{\"view\":");
        book.toJsonView(params, out);
        // Multiple orders may be updated if one trades with one's self.
        out.append(",\"orders\":[");
        order.toJson(params, out);
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
        SlNode node = execs.getFirst();
        while (node != null) {
            node = popNext(node);
        }
        matches.clear();
        execs.clear();
    }

    public final Order getOrder() {
        return order;
    }

    public final SlNode getFirstExec() {
        return execs.getFirst();
    }

    public final boolean isEmptyExec() {
        return execs.isEmpty();
    }

    public final Posn getPosn() {
        return posn;
    }
}
