/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.entity;

import static com.swirlycloud.swirly.date.JulianDay.ymdToJd;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Before;
import org.junit.Test;

import com.swirlycloud.swirly.entity.Factory;
import com.swirlycloud.swirly.entity.Posn;
import com.swirlycloud.swirly.entity.TraderSess;
import com.swirlycloud.swirly.mock.MockTrader;

public final class TraderSessTest {

    private static final int TODAY = ymdToJd(2014, 2, 12);
    private static final int SETTL_DAY = TODAY + 2;

    private Factory factory;

    @Before
    public final void setUp() {
        factory = new BasicFactory() {

            private static final int CAPACITY = 1 << 5; // 64

            private final @NonNull RequestRefMap refIdx = new RequestRefMap(CAPACITY);

            @Override
            public final @NonNull TraderSess newTrader(@NonNull String mnem,
                    @Nullable String display, @NonNull String email) {
                return new TraderSess(mnem, display, email, refIdx, this);
            }
        };
    }

    @Test
    public final void testAdd() {
        final TraderSess sess = (TraderSess) MockTrader.newTrader("MARAYL", factory);
        final Posn posn1 = factory.newPosn(sess.getMnem(), "EURUSD", SETTL_DAY);
        posn1.addBuy(10, 12344 * 10);
        posn1.addSell(15, 12346 * 15);
        assertSame(posn1, sess.addPosn(posn1));
        final Posn posn2 = factory.newPosn(sess.getMnem(), "EURUSD", SETTL_DAY);
        posn2.addBuy(15, 12343 * 15);
        posn2.addSell(10, 12347 * 10);
        assertSame(posn1, sess.addPosn(posn2));
        assertEquals(12344 * 10 + 12343 * 15, posn1.getBuyCost());
        assertEquals(25, posn1.getBuyLots());
        assertEquals(12346 * 15 + 12347 * 10, posn1.getSellCost());
        assertEquals(25, posn1.getSellLots());
    }
}
