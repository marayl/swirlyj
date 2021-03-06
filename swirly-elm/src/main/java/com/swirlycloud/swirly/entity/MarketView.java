/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.entity;

import static com.swirlycloud.swirly.date.JulianDay.jdToIso;

import java.io.IOException;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.swirly.date.JulianDay;
import com.swirlycloud.swirly.domain.MarketData;
import com.swirlycloud.swirly.node.AbstractRbNode;
import com.swirlycloud.swirly.util.JsonUtil;
import com.swirlycloud.swirly.util.Params;

/**
 * A flattened view of a market.
 * 
 * @author Mark Aylett
 */
public final @NonNullByDefault class MarketView extends AbstractRbNode implements Financial {

    private static final long serialVersionUID = 1L;

    /**
     * Maximum price levels in view.
     */
    private static final int DEPTH_MAX = 5;

    private final String market;
    private final String contr;
    private final int settlDay;
    private long lastLots;
    private long lastTicks;
    private long lastTime;
    private final MarketData data;

    private static void parseArray(JsonParser p, MarketData data, int col) throws IOException {
        for (int row = 0; p.hasNext(); ++row) {
            final Event event = p.next();
            switch (event) {
            case END_ARRAY:
                return;
            case VALUE_NULL:
                data.setValue(row, col, 0);
                break;
            case VALUE_NUMBER:
                data.setValue(row, col, p.getLong());
                break;
            default:
                throw new IOException(String.format("unexpected json token '%s'", event));
            }
        }
        throw new IOException("end-of array not found");
    }

    public MarketView(String market, String contr, int settlDay, long lastLots, long lastTicks,
            long lastTime, MarketData data) {
        this.market = market;
        this.contr = contr;
        this.settlDay = settlDay;
        this.lastLots = lastLots;
        this.lastTicks = lastTicks;
        this.lastTime = lastTime;
        this.data = data;
    }

    public MarketView(Financial fin, long lastLots, long lastTicks, long lastTime,
            MarketData data) {
        this.market = fin.getMarket();
        this.contr = fin.getContr();
        this.settlDay = fin.getSettlDay();
        this.lastLots = lastLots;
        this.lastTicks = lastTicks;
        this.lastTime = lastTime;
        this.data = data;
    }

    public MarketView(String market, String contr, int settlDay, long lastLots, long lastTicks,
            long lastTime) {
        this.market = market;
        this.contr = contr;
        this.settlDay = settlDay;
        this.lastLots = lastLots;
        this.lastTicks = lastTicks;
        this.lastTime = lastTime;
        this.data = new MarketData();
    }

    public MarketView(Financial fin, long lastLots, long lastTicks, long lastTime) {
        this.market = fin.getMarket();
        this.contr = fin.getContr();
        this.settlDay = fin.getSettlDay();
        this.lastLots = lastLots;
        this.lastTicks = lastTicks;
        this.lastTime = lastTime;
        this.data = new MarketData();
    }

    public static MarketView parse(JsonParser p) throws IOException {
        String market = null;
        String contr = null;
        int settlDay = 0;
        long lastLots = 0;
        long lastTicks = 0;
        long lastTime = 0;
        final MarketData data = new MarketData();

        String name = null;
        while (p.hasNext()) {
            final Event event = p.next();
            switch (event) {
            case END_OBJECT:
                if (market == null) {
                    throw new IOException("market is null");
                }
                if (contr == null) {
                    throw new IOException("contr is null");
                }
                return new MarketView(market, contr, settlDay, lastLots, lastTicks, lastTime, data);
            case KEY_NAME:
                name = p.getString();
                break;
            case START_ARRAY:
                if ("bidTicks".equals(name)) {
                    parseArray(p, data, MarketData.BID_TICKS);
                } else if ("bidResd".equals(name)) {
                    parseArray(p, data, MarketData.BID_RESD);
                } else if ("bidCount".equals(name)) {
                    parseArray(p, data, MarketData.BID_COUNT);
                } else if ("offerTicks".equals(name)) {
                    parseArray(p, data, MarketData.OFFER_TICKS);
                } else if ("offerResd".equals(name)) {
                    parseArray(p, data, MarketData.OFFER_RESD);
                } else if ("offerCount".equals(name)) {
                    parseArray(p, data, MarketData.OFFER_COUNT);
                } else {
                    throw new IOException(String.format("unexpected array field '%s'", name));
                }
                break;
            case VALUE_NULL:
                if ("settlDate".equals(name)) {
                    settlDay = 0;
                } else if ("lastLots".equals(name)) {
                    lastLots = 0;
                } else if ("lastTicks".equals(name)) {
                    lastTicks = 0;
                } else if ("lastTime".equals(name)) {
                    lastTime = 0;
                } else {
                    throw new IOException(String.format("unexpected null field '%s'", name));
                }
                break;
            case VALUE_NUMBER:
                if ("settlDate".equals(name)) {
                    settlDay = JulianDay.maybeIsoToJd(p.getInt());
                } else if ("lastLots".equals(name)) {
                    lastLots = p.getLong();
                } else if ("lastTicks".equals(name)) {
                    lastTicks = p.getLong();
                } else if ("lastTime".equals(name)) {
                    lastTime = p.getLong();
                } else {
                    throw new IOException(String.format("unexpected number field '%s'", name));
                }
                break;
            case VALUE_STRING:
                if ("market".equals(name)) {
                    market = p.getString();
                } else if ("contr".equals(name)) {
                    contr = p.getString();
                } else {
                    throw new IOException(String.format("unexpected string field '%s'", name));
                }
                break;
            default:
                throw new IOException(String.format("unexpected json token '%s'", event));
            }
        }
        throw new IOException("end-of object not found");
    }

    @Override
    public final int hashCode() {
        return market.hashCode();
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MarketView other = (MarketView) obj;
        if (!market.equals(other.market)) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        return JsonUtil.toJson(this);
    }

    @Override
    public final void toJson(@Nullable Params params, Appendable out) throws IOException {
        int depth = 3; // Default depth.
        if (params != null) {
            final Integer val = params.getParam("depth", Integer.class);
            if (val != null) {
                depth = val.intValue();
            }
        }
        // Round-up to minimum.
        depth = Math.max(depth, 1);
        // Round-down to maximum.
        depth = Math.min(depth, DEPTH_MAX);

        out.append("{\"market\":\"").append(market);
        out.append("\",\"contr\":\"").append(contr);
        out.append("\",\"settlDate\":");
        if (settlDay != 0) {
            out.append(String.valueOf(jdToIso(settlDay)));
        } else {
            out.append("null");
        }
        if (lastLots != 0) {
            out.append(",\"lastLots\":").append(String.valueOf(lastLots));
            out.append(",\"lastTicks\":").append(String.valueOf(lastTicks));
            out.append(",\"lastTime\":").append(String.valueOf(lastTime));
        } else {
            out.append(",\"lastLots\":null,\"lastTicks\":null,\"lastTime\":null");
        }
        out.append(",\"bidTicks\":[");
        for (int i = 0; i < depth; ++i) {
            if (i > 0) {
                out.append(',');
            }
            if (isValidBid(i)) {
                out.append(String.valueOf(getBidTicks(i)));
            } else {
                out.append("null");
            }
        }
        out.append("],\"bidResd\":[");
        for (int i = 0; i < depth; ++i) {
            if (i > 0) {
                out.append(',');
            }
            if (isValidBid(i)) {
                out.append(String.valueOf(getBidResd(i)));
            } else {
                out.append("null");
            }
        }
        out.append("],\"bidCount\":[");
        for (int i = 0; i < depth; ++i) {
            if (i > 0) {
                out.append(',');
            }
            if (isValidBid(i)) {
                out.append(String.valueOf(getBidCount(i)));
            } else {
                out.append("null");
            }
        }
        out.append("],\"offerTicks\":[");
        for (int i = 0; i < depth; ++i) {
            if (i > 0) {
                out.append(',');
            }
            if (isValidOffer(i)) {
                out.append(String.valueOf(getOfferTicks(i)));
            } else {
                out.append("null");
            }
        }
        out.append("],\"offerResd\":[");
        for (int i = 0; i < depth; ++i) {
            if (i > 0) {
                out.append(',');
            }
            if (isValidOffer(i)) {
                out.append(String.valueOf(getOfferResd(i)));
            } else {
                out.append("null");
            }
        }
        out.append("],\"offerCount\":[");
        for (int i = 0; i < depth; ++i) {
            if (i > 0) {
                out.append(',');
            }
            if (isValidOffer(i)) {
                out.append(String.valueOf(getOfferCount(i)));
            } else {
                out.append("null");
            }
        }
        out.append("]}");
    }

    public final void setLastLots(long lastLots) {
        this.lastLots = lastLots;
    }

    public final void setLastTicks(long lastTicks) {
        this.lastTicks = lastTicks;
    }

    public final void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public final String getMarket() {
        return market;
    }

    @Override
    public final String getContr() {
        return contr;
    }

    @Override
    public final int getSettlDay() {
        return settlDay;
    }

    @Override
    public final boolean isSettlDaySet() {
        return settlDay != 0;
    }

    public final long getLastLots() {
        return lastLots;
    }

    public final long getLastTicks() {
        return lastTicks;
    }

    public final long getLastTime() {
        return lastTime;
    }

    public final MarketData getData() {
        return data;
    }

    public final boolean isValidBid(int row) {
        return data.isValidBid(row);
    }

    public final long getBidTicks(int row) {
        return data.getBidTicks(row);
    }

    public final long getBidResd(int row) {
        return data.getBidResd(row);
    }

    public final int getBidCount(int row) {
        return data.getBidCount(row);
    }

    public final boolean isValidOffer(int row) {
        return data.isValidOffer(row);
    }

    public final long getOfferTicks(int row) {
        return data.getOfferTicks(row);
    }

    public final long getOfferResd(int row) {
        return data.getOfferResd(row);
    }

    public final int getOfferCount(int row) {
        return data.getOfferCount(row);
    }
}
