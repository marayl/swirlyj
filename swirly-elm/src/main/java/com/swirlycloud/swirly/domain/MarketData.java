/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.domain;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class MarketData implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int ROWS = 5;
    public static final int COLS = 6;

    /**
     * Maximum rungs in ladder.
     */
    public static final int DEPTH_MAX = ROWS;

    // Columns.
    public static final int BID_TICKS = 0;
    public static final int BID_RESD = 1;
    public static final int BID_COUNT = 2;
    public static final int OFFER_TICKS = 3;
    public static final int OFFER_RESD = 4;
    public static final int OFFER_COUNT = 5;

    private final long[] data;

    public MarketData() {
        data = new long[ROWS * COLS];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROWS; ++i) {
            if (0 < i) {
                sb.append('\n');
            }
            for (int j = 0; j < COLS; ++j) {
                if (0 < j) {
                    sb.append(' ');
                }
                sb.append(data[i * COLS + j]);
            }
        }
        return sb.toString();
    }

    public final void clear() {
        final int len = data.length;
        for (int i = 0; i < len; ++i) {
            data[i] = 0;
        }
    }

    public final void setValue(int row, int col, long value) {
        // Assert only because this needs to be fast.
        assert 0 <= row && row < ROWS;
        assert 0 <= col && col < COLS;
        data[row * COLS + col] = value;
    }

    public final long getValue(int row, int col) {
        // Assert only because this needs to be fast.
        assert 0 <= row && row < ROWS;
        assert 0 <= col && col < COLS;
        return data[row * COLS + col];
    }

    public final int getCols() {
        return COLS;
    }

    public final int getRows() {
        return ROWS;
    }

    public final void setBidLevel(int row, long ticks, long resd, long count) {
        setValue(row, BID_TICKS, ticks);
        setValue(row, BID_RESD, resd);
        setValue(row, BID_COUNT, count);
    }

    public final void setBidTicks(int row, long ticks) {
        setValue(row, BID_TICKS, ticks);
    }

    public final void setBidResd(int row, long resd) {
        setValue(row, BID_RESD, resd);
    }

    public final void setBidCount(int row, long count) {
        setValue(row, BID_COUNT, count);
    }

    public final void setOfferLevel(int row, long ticks, long resd, long count) {
        setValue(row, OFFER_TICKS, ticks);
        setValue(row, OFFER_RESD, resd);
        setValue(row, OFFER_COUNT, count);
    }

    public final void setOfferTicks(int row, long ticks) {
        setValue(row, OFFER_TICKS, ticks);
    }

    public final void setOfferResd(int row, long resd) {
        setValue(row, OFFER_RESD, resd);
    }

    public final void setOfferCount(int row, long count) {
        setValue(row, OFFER_COUNT, count);
    }

    public final boolean isValidBid(int row) {
        // Use lots instead of ticks or count, because zero is a valid price, and order counts may
        // be unavailable.
        return getBidResd(row) > 0;
    }

    public final long getBidTicks(int row) {
        return getValue(row, BID_TICKS);
    }

    public final long getBidResd(int row) {
        return getValue(row, BID_RESD);
    }

    public final int getBidCount(int row) {
        return (int) getValue(row, BID_COUNT);
    }

    public final boolean isValidOffer(int row) {
        // Use lots instead of ticks or count, because zero is a valid price, and order counts may
        // be unavailable.
        return getOfferResd(row) > 0;
    }

    public final long getOfferTicks(int row) {
        return getValue(row, OFFER_TICKS);
    }

    public final long getOfferResd(int row) {
        return getValue(row, OFFER_RESD);
    }

    public final int getOfferCount(int row) {
        return (int) getValue(row, OFFER_COUNT);
    }
}
