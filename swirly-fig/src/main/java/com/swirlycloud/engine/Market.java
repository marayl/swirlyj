/*******************************************************************************
 * Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>
 *
 * All rights reserved.
 *******************************************************************************/
package com.swirlycloud.engine;

import static com.swirlycloud.util.Date.jdToIso;

import com.swirlycloud.domain.Action;
import com.swirlycloud.domain.Contr;
import com.swirlycloud.domain.Level;
import com.swirlycloud.domain.Order;
import com.swirlycloud.domain.Side;
import com.swirlycloud.util.BasicRbNode;
import com.swirlycloud.util.Date;
import com.swirlycloud.util.Identifiable;
import com.swirlycloud.util.Printable;
import com.swirlycloud.util.RbNode;

public final class Market extends BasicRbNode implements Identifiable, Printable {
    /**
     * Maximum price levels in view.
     */
    private static final int DEPTH_MAX = 5;

    private final long id;
    private final Contr contr;
    private final int settlDay;
    private final Side bidSide = new Side();
    private final Side offerSide = new Side();

    private final Side side(Action action) {
        return action == Action.BUY ? bidSide : offerSide;
    }

    private final void printTob(StringBuilder sb) {
        sb.append("{\"id\":").append(id);
        sb.append(",\"contr\":\"").append(contr.getMnem());
        sb.append("\",\"settlDate\":").append(jdToIso(settlDay));

        final Level firstBid = (Level) bidSide.getFirstLevel();
        if (firstBid != null) {
            sb.append(",\"bidTicks\":").append(firstBid.getTicks());
            sb.append(",\"bidLots\":").append(firstBid.getLots());
            sb.append(",\"bidCount\":").append(firstBid.getCount());
        } else {
            sb.append(",\"bidTicks\":0,\"bidLots\":0,\"bidCount\":0");
        }
        final Level firstOffer = (Level) offerSide.getFirstLevel();
        if (firstOffer != null) {
            sb.append(",\"offerTicks\":").append(firstOffer.getTicks());
            sb.append(",\"offerLots\":").append(firstOffer.getLots());
            sb.append(",\"offerCount\":").append(firstOffer.getCount());
        } else {
            sb.append(",\"offerTicks\":0,\"offerLots\":0,\"offerCount\":0");
        }
        sb.append("}");
    }

    private final void printDepth(StringBuilder sb, int levels) {
        sb.append("{\"id\":").append(id);
        sb.append(",\"contr\":\"").append(contr.getMnem());
        sb.append("\",\"settlDate\":").append(jdToIso(settlDay));
        sb.append(",\"bidTicks\":[");

        final RbNode firstBid = bidSide.getFirstLevel();
        final RbNode firstOffer = offerSide.getFirstLevel();

        RbNode node = firstBid;
        for (int i = 0; i < levels; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (node != null) {
                final Level level = (Level) node;
                sb.append(level.getTicks());
                node = node.rbNext();
            } else {
                sb.append('0');
            }
        }
        sb.append("],\"bidLots\":[");
        node = firstBid;
        for (int i = 0; i < levels; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (node != null) {
                final Level level = (Level) node;
                sb.append(level.getLots());
                node = node.rbNext();
            } else {
                sb.append('0');
            }
        }
        sb.append("],\"bidCount\":[");
        node = firstBid;
        for (int i = 0; i < levels; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (node != null) {
                final Level level = (Level) node;
                sb.append(level.getCount());
                node = node.rbNext();
            } else {
                sb.append('0');
            }
        }
        sb.append("],\"offerTicks\":[");
        node = firstOffer;
        for (int i = 0; i < levels; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (node != null) {
                final Level level = (Level) node;
                sb.append(level.getTicks());
                node = node.rbNext();
            } else {
                sb.append('0');
            }
        }
        sb.append("],\"offerLots\":[");
        node = firstOffer;
        for (int i = 0; i < levels; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (node != null) {
                final Level level = (Level) node;
                sb.append(level.getLots());
                node = node.rbNext();
            } else {
                sb.append('0');
            }
        }
        sb.append("],\"offerCount\":[");
        node = firstOffer;
        for (int i = 0; i < levels; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (node != null) {
                final Level level = (Level) node;
                sb.append(level.getCount());
                node = node.rbNext();
            } else {
                sb.append('0');
            }
        }
        sb.append("]}");
    }

    public Market(Contr contr, int settlDay) {
        this.id = toId(contr.getId(), settlDay);
        this.contr = contr;
        this.settlDay = settlDay;
    }

    /**
     * Synthetic market id.
     */

    public static long toId(long contrId, int settlDay) {
        // 16 bit contr-id.
        final int CONTR_MASK = (1 << 16) - 1;
        // 16 bits is sufficient for truncated Julian day.
        final int TJD_MASK = (1 << 16) - 1;

        // Truncated Julian Day (TJD).
        final long tjd = Date.jdToTjd(settlDay);
        return ((contrId & CONTR_MASK) << 16) | (tjd & TJD_MASK);
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        print(sb, null);
        return sb.toString();
    }

    @Override
    public final void print(StringBuilder sb, Object arg) {
        int levels = 1;
        if (arg != null) {
            levels = (Integer) arg;
        }
        // Round-up to minimum.
        levels = Math.max(levels, 1);
        // Round-down to maximum.
        levels = Math.min(levels, DEPTH_MAX);
        if (levels == 1) {
            printTob(sb);
        } else {
            printDepth(sb, levels);
        }
    }

    public final void insertOrder(Order order) {
        side(order.getAction()).insertOrder(order);
    }

    public final void removeOrder(Order order) {
        side(order.getAction()).removeOrder(order);
    }

    public final void placeOrder(Order order, long now) {
        side(order.getAction()).placeOrder(order, now);
    }

    public final void reviseOrder(Order order, long lots, long now) {
        side(order.getAction()).reviseOrder(order, lots, now);
    }

    public final void cancelOrder(Order order, long now) {
        side(order.getAction()).cancelOrder(order, now);
    }

    public final void takeOrder(Order order, long lots, long now) {
        side(order.getAction()).takeOrder(order, lots, now);
    }

    @Override
    public final long getId() {
        return id;
    }

    public final Contr getContr() {
        return contr;
    }

    public final int getSettlDay() {
        return settlDay;
    }

    public final Side getBidSide() {
        return bidSide;
    }

    public final Side getOfferSide() {
        return offerSide;
    }
}