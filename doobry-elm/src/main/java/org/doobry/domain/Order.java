/*******************************************************************************
 * Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>
 *
 * All rights reserved.
 *******************************************************************************/
package org.doobry.domain;

import org.doobry.util.BasicRbDlNode;
import org.doobry.util.Identifiable;
import org.doobry.util.RbNode;

public final class Order extends BasicRbDlNode implements Identifiable, Instruct {

    // Internals.
    // Singly-linked buckets.
    transient Order nextRef;
    transient RbNode level;

    private final long id;
    /**
     * The executing trader.
     */
    private Identifiable trader;
    /**
     * The give-up counter-party.
     */
    private Identifiable giveup;
    private Identifiable contr;
    private final int settlDay;
    /**
     * Ref is optional.
     */
    private final String ref;
    State state;
    private final Action action;
    private final long ticks;
    /**
     * Must be greater than zero.
     */
    long lots;
    /**
     * Must be greater than zero.
     */
    long resd;
    /**
     * Must not be greater that lots.
     */
    long exec;
    long lastTicks;
    long lastLots;
    /**
     * Minimum to be filled by this
     */
    private final long minLots;
    long created;
    long modified;

    public Order(long id, Identifiable trader, Identifiable giveup, Identifiable contr,
            int settlDay, String ref, Action action, long ticks, long lots, long minLots,
            long created) {
        assert trader != null;
        assert giveup != null;
        assert contr != null;
        assert lots > 0 && lots >= minLots;
        this.id = id;
        this.trader = trader;
        this.giveup = giveup;
        this.contr = contr;
        this.settlDay = settlDay;
        this.ref = ref;
        this.state = State.NEW;
        this.action = action;
        this.ticks = ticks;
        this.lots = lots;
        this.resd = lots;
        this.exec = 0;
        this.lastTicks = 0;
        this.lastLots = 0;
        this.minLots = minLots;
        this.created = created;
        this.modified = created;
    }

    public final void enrich(Party trader, Party giveup, Contr contr) {
        assert this.trader.getId() == trader.getId();
        assert this.giveup.getId() == giveup.getId();
        assert this.contr.getId() == contr.getId();
        this.trader = trader;
        this.giveup = giveup;
        this.contr = contr;
    }

    public final void place(long now) {
        assert lots > 0 && lots >= minLots;
        state = State.NEW;
        resd = lots;
        exec = 0;
        modified = now;
    }

    public final void revise(long lots, long now) {
        assert lots > 0;
        assert lots >= exec && lots >= minLots && lots <= lots;
        final long delta = this.lots - lots;
        assert delta >= 0;
        state = State.REVISE;
        this.lots = lots;
        resd -= delta;
        modified = now;
    }

    public final void cancel(long now) {
        state = State.CANCEL;
        // Note that executed lots is not affected.
        resd = 0;
        modified = now;
    }

    public final void trade(long lots, long lastTicks, long lastLots, long now) {
        state = State.TRADE;
        resd -= lots;
        this.exec += lots;
        this.lastTicks = lastTicks;
        this.lastLots = lastLots;
        modified = now;
    }

    @Override
    public final long getKey() {
        return id;
    }

    @Override
    public final long getId() {
        return id;
    }

    @Override
    public final long getTraderId() {
        return trader.getId();
    }

    @Override
    public final Party getTrader() {
        return (Party) trader;
    }

    @Override
    public final long getGiveupId() {
        return giveup.getId();
    }

    @Override
    public final Party getGiveup() {
        return (Party) giveup;
    }

    @Override
    public final long getContrId() {
        return contr.getId();
    }

    @Override
    public final Contr getContr() {
        return (Contr) contr;
    }

    @Override
    public final int getSettlDay() {
        return settlDay;
    }

    @Override
    public final String getRef() {
        return ref;
    }

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final Action getAction() {
        return action;
    }

    @Override
    public final long getTicks() {
        return ticks;
    }

    @Override
    public final long getLots() {
        return lots;
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

    @Override
    public final boolean isDone() {
        return resd == 0;
    }

    public final long getCreated() {
        return created;
    }

    public final long getModified() {
        return modified;
    }
}