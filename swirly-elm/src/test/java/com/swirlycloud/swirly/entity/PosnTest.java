/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.entity;

import static com.swirlycloud.swirly.date.JulianDay.ymdToJd;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.swirlycloud.swirly.date.JulianDay;
import com.swirlycloud.swirly.entity.BasicFactory;
import com.swirlycloud.swirly.entity.Factory;
import com.swirlycloud.swirly.entity.Posn;

public final class PosnTest {
    private static final Factory FACTORY = new BasicFactory();

    private static final int TODAY = ymdToJd(2014, 2, 12);
    private static final int SETTL_DAY = TODAY + 2;

    @Test
    public final void testAdd() {
        final Posn posn = FACTORY.newPosn("MARAYL", "EURUSD", SETTL_DAY);
        assertEquals("MARAYL", posn.getTrader());
        assertEquals("EURUSD", posn.getContr());
        assertEquals(SETTL_DAY, posn.getSettlDay());
        assertEquals(0, posn.getBuyCost());
        assertEquals(0, posn.getBuyLots());
        assertEquals(0, posn.getSellCost());
        assertEquals(0, posn.getSellLots());

        posn.addBuy(10, 12344 * 10);
        assertEquals(12344 * 10, posn.getBuyCost());
        assertEquals(10, posn.getBuyLots());
        assertEquals(0, posn.getSellCost());
        assertEquals(0, posn.getSellLots());

        posn.addSell(15, 12346 * 15);
        assertEquals(12344 * 10, posn.getBuyCost());
        assertEquals(10, posn.getBuyLots());
        assertEquals(12346 * 15, posn.getSellCost());
        assertEquals(15, posn.getSellLots());

        posn.addBuy(15, 12343 * 15);
        posn.addSell(10, 12347 * 10);
        assertEquals(12344 * 10 + 12343 * 15, posn.getBuyCost());
        assertEquals(25, posn.getBuyLots());
        assertEquals(12346 * 15 + 12347 * 10, posn.getSellCost());
        assertEquals(25, posn.getSellLots());
    }

    @Test
    public final void testSettlDay() {
        final Posn posn = FACTORY.newPosn("MARAYL", "EURUSD", SETTL_DAY);
        assertEquals(SETTL_DAY, posn.getSettlDay());
        assertTrue(posn.isSettlDaySet());
        posn.setSettlDay(0);
        assertFalse(posn.isSettlDaySet());
    }

    @Test
    public final void testToString() {
        final Posn posn = FACTORY.newPosn("MARAYL", "EURUSD", JulianDay.isoToJd(20140314));
        posn.setBuyCost(1);
        posn.setBuyLots(2);
        posn.setSellCost(3);
        posn.setSellLots(4);
        assertEquals(
                "{\"trader\":\"MARAYL\",\"contr\":\"EURUSD\",\"settlDate\":20140314,\"buyLots\":2,\"buyCost\":1,\"sellLots\":4,\"sellCost\":3}",
                posn.toString());
    }
}
