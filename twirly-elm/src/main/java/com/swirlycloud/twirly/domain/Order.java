/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.domain;

import static com.swirlycloud.twirly.date.JulianDay.jdToIso;

import java.io.IOException;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.twirly.date.JulianDay;
import com.swirlycloud.twirly.node.DlNode;
import com.swirlycloud.twirly.node.DlUtil;
import com.swirlycloud.twirly.node.RbNode;
import com.swirlycloud.twirly.util.Params;

/**
 * An instruction to buy or sell goods or services.
 * 
 * @author Mark Aylett
 */
public final @NonNullByDefault class Order extends AbstractRequest implements DlNode, Instruct {

    private static final long serialVersionUID = 1L;

    private transient DlNode dlPrev = DlUtil.NULL;
    private transient DlNode dlNext = DlUtil.NULL;

    // Internals.
    transient @Nullable RbNode level;

    State state;
    private final long ticks;
    /**
     * Must be greater than zero.
     */
    long resd;
    /**
     * Must not be greater that lots.
     */
    long exec;
    long cost;
    long lastTicks;
    long lastLots;
    /**
     * Minimum to be filled by this
     */
    private final long minLots;
    transient long quot;
    private final boolean pecan;
    long modified;

    Order(long id, String trader, String market, String contr, int settlDay, @Nullable String ref,
            State state, Side side, long ticks, long lots, long resd, long exec, long cost,
            long lastTicks, long lastLots, long minLots, boolean pecan, long created,
            long modified) {
        super(id, trader, market, contr, settlDay, ref, side, lots, created);
        assert lots > 0 && lots >= minLots;
        this.state = state;
        this.ticks = ticks;
        this.resd = resd;
        this.exec = exec;
        this.cost = cost;
        this.lastTicks = lastTicks;
        this.lastLots = lastLots;
        this.minLots = minLots;
        this.quot = 0;
        this.pecan = pecan;
        this.modified = modified;
    }

    public static Order parse(JsonParser p) throws IOException {
        long id = 0;
        String trader = null;
        String market = null;
        String contr = null;
        int settlDay = 0;
        String ref = null;
        State state = null;
        Side side = null;
        long ticks = 0;
        long lots = 0;
        long resd = 0;
        long exec = 0;
        long cost = 0;
        long lastTicks = 0;
        long lastLots = 0;
        long minLots = 0;
        boolean pecan = false;
        long created = 0;
        long modified = 0;

        String name = null;
        while (p.hasNext()) {
            final Event event = p.next();
            switch (event) {
            case END_OBJECT:
                if (trader == null) {
                    throw new IOException("trader is null");
                }
                if (market == null) {
                    throw new IOException("market is null");
                }
                if (contr == null) {
                    throw new IOException("contr is null");
                }
                if (state == null) {
                    throw new IOException("state is null");
                }
                if (side == null) {
                    throw new IOException("side is null");
                }
                return new Order(id, trader, market, contr, settlDay, ref, state, side, ticks, lots,
                        resd, exec, cost, lastTicks, lastLots, minLots, pecan, created, modified);
            case KEY_NAME:
                name = p.getString();
                break;
            case VALUE_FALSE:
                if ("pecan".equals(name)) {
                    pecan = false;
                }
                break;
            case VALUE_NULL:
                if ("settlDate".equals(name)) {
                    settlDay = 0;
                } else if ("ref".equals(name)) {
                    ref = "";
                } else if ("lastTicks".equals(name)) {
                    lastTicks = 0;
                } else if ("lastLots".equals(name)) {
                    lastLots = 0;
                } else {
                    throw new IOException(String.format("unexpected null field '%s'", name));
                }
                break;
            case VALUE_NUMBER:
                if ("id".equals(name)) {
                    id = p.getLong();
                } else if ("settlDate".equals(name)) {
                    settlDay = JulianDay.maybeIsoToJd(p.getInt());
                } else if ("ticks".equals(name)) {
                    ticks = p.getLong();
                } else if ("lots".equals(name)) {
                    lots = p.getLong();
                } else if ("resd".equals(name)) {
                    resd = p.getLong();
                } else if ("exec".equals(name)) {
                    exec = p.getLong();
                } else if ("cost".equals(name)) {
                    cost = p.getLong();
                } else if ("lastTicks".equals(name)) {
                    lastTicks = p.getLong();
                } else if ("lastLots".equals(name)) {
                    lastLots = p.getLong();
                } else if ("minLots".equals(name)) {
                    minLots = p.getLong();
                } else if ("created".equals(name)) {
                    created = p.getLong();
                } else if ("modified".equals(name)) {
                    modified = p.getLong();
                } else {
                    throw new IOException(String.format("unexpected number field '%s'", name));
                }
                break;
            case VALUE_STRING:
                if ("trader".equals(name)) {
                    trader = p.getString();
                } else if ("market".equals(name)) {
                    market = p.getString();
                } else if ("contr".equals(name)) {
                    contr = p.getString();
                } else if ("ref".equals(name)) {
                    ref = p.getString();
                } else if ("state".equals(name)) {
                    final String s = p.getString();
                    assert s != null;
                    state = State.valueOf(s);
                } else if ("side".equals(name)) {
                    final String s = p.getString();
                    assert s != null;
                    side = Side.valueOf(s);
                } else {
                    throw new IOException(String.format("unexpected string field '%s'", name));
                }
                break;
            case VALUE_TRUE:
                if ("pecan".equals(name)) {
                    pecan = true;
                }
                break;
            default:
                throw new IOException(String.format("unexpected json token '%s'", event));
            }
        }
        throw new IOException("end-of object not found");
    }

    @Override
    public final void toJson(@Nullable Params params, Appendable out) throws IOException {
        out.append("{\"id\":").append(String.valueOf(id));
        out.append(",\"trader\":\"").append(trader);
        out.append("\",\"market\":\"").append(market);
        out.append("\",\"contr\":\"").append(contr);
        out.append("\",\"settlDate\":");
        if (settlDay != 0) {
            out.append(String.valueOf(jdToIso(settlDay)));
        } else {
            out.append("null");
        }
        out.append(",\"ref\":");
        if (ref != null) {
            out.append('"').append(ref).append('"');
        } else {
            out.append("null");
        }
        out.append(",\"state\":\"").append(state.name());
        out.append("\",\"side\":\"").append(side.name());
        out.append("\",\"ticks\":").append(String.valueOf(ticks));
        out.append(",\"lots\":").append(String.valueOf(lots));
        out.append(",\"resd\":").append(String.valueOf(resd));
        out.append(",\"exec\":").append(String.valueOf(exec));
        out.append(",\"cost\":").append(String.valueOf(cost));
        if (lastLots != 0) {
            out.append(",\"lastTicks\":").append(String.valueOf(lastTicks));
            out.append(",\"lastLots\":").append(String.valueOf(lastLots));
        } else {
            out.append(",\"lastTicks\":null,\"lastLots\":null");
        }
        out.append(",\"minLots\":").append(String.valueOf(minLots));
        out.append(",\"pecan\":").append(String.valueOf(pecan));
        out.append(",\"created\":").append(String.valueOf(created));
        out.append(",\"modified\":").append(String.valueOf(modified));
        out.append("}");
    }

    @Override
    public final void insert(DlNode prev, DlNode next) {

        prev.setDlNext(this);
        this.setDlPrev(prev);

        next.setDlPrev(this);
        this.setDlNext(next);
    }

    @Override
    public final void insertBefore(DlNode next) {
        insert(next.dlPrev(), next);
    }

    @Override
    public final void insertAfter(DlNode prev) {
        insert(prev, prev.dlNext());
    }

    @Override
    public final void remove() {
        dlNext().setDlPrev(dlPrev);
        dlPrev().setDlNext(dlNext);
        setDlPrev(DlUtil.NULL);
        setDlNext(DlUtil.NULL);
    }

    @Override
    public void setDlPrev(@NonNull DlNode prev) {
        this.dlPrev = prev;
    }

    @Override
    public void setDlNext(@NonNull DlNode next) {
        this.dlNext = next;
    }

    @Override
    public final DlNode dlNext() {
        return this.dlNext;
    }

    @Override
    public final DlNode dlPrev() {
        return this.dlPrev;
    }

    @Override
    public boolean isEnd() {
        return false;
    }

    final void create(long now) {
        assert lots > 0 && lots >= minLots;
        state = State.NEW;
        resd = lots;
        exec = 0;
        cost = 0;
        modified = now;
    }

    final void revise(long lots, long now) {
        assert lots > 0;
        assert lots >= exec && lots >= minLots && lots <= this.lots;
        final long delta = this.lots - lots;
        assert delta >= 0;
        state = State.REVISE;
        this.lots = lots;
        resd -= delta;
        modified = now;
    }

    final void cancel(long now) {
        if (quot == 0) {
            state = State.CANCEL;
            // Note that executed lots is not affected.
            resd = 0;
        } else {
            state = State.PECAN;
        }
        modified = now;
    }

    public final void trade(long takenLots, long takenCost, long lastTicks, long lastLots,
            long now) {
        state = State.TRADE;
        resd -= takenLots;
        this.exec += takenLots;
        this.cost += takenCost;
        this.lastTicks = lastTicks;
        this.lastLots = lastLots;
        modified = now;
    }

    final void trade(long lastTicks, long lastLots, long now) {
        trade(lastLots, lastLots * lastTicks, lastTicks, lastLots, now);
    }

    @Override
    public final long getOrderId() {
        return id;
    }

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final long getTicks() {
        return ticks;
    }

    @Override
    public final long getResd() {
        return resd;
    }

    @Override
    public final long getExec() {
        return exec;
    }

    @Override
    public final long getCost() {
        return cost;
    }

    @Override
    public final double getAvgTicks() {
        return exec != 0 ? (double) cost / exec : 0;
    }

    @Override
    public final long getLastTicks() {
        return lastTicks;
    }

    @Override
    public final long getLastLots() {
        return lastLots;
    }

    @Override
    public final long getMinLots() {
        return minLots;
    }

    public final long getQuot() {
        return quot;
    }

    public final long getAvail() {
        return resd - quot;
    }

    @Override
    public final boolean isDone() {
        return resd == 0;
    }

    public final boolean isPecan() {
        return pecan;
    }

    public final long getModified() {
        return modified;
    }
}
