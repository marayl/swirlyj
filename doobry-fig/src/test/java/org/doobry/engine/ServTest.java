/*******************************************************************************
 * Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>
 *
 * All rights reserved.
 *******************************************************************************/
package org.doobry.engine;

import static org.doobry.util.Date.ymdToJd;
import static org.junit.Assert.assertEquals;

import org.doobry.domain.Action;
import org.doobry.domain.Order;
import org.doobry.domain.Reg;
import org.doobry.domain.State;
import org.doobry.mock.MockBank;
import org.doobry.mock.MockJourn;
import org.doobry.mock.MockModel;
import org.junit.Test;

public final class ServTest {
    @Test
    public final void test() {
        final Serv s = new Serv(new MockBank(Reg.values().length), new MockJourn());
        try {
            s.load(new MockModel());
            final Accnt trader = s.getLazyAccnt("WRAMIREZ");
            final Accnt giveup = s.getLazyAccnt("DBRA");
            final int settlDay = ymdToJd(2014, 3, 14);
            final Book book = s.getLazyBook("EURUSD", settlDay);
            final Order order = s.placeOrder(trader, giveup, book, "", Action.BUY, 12345, 1, 1);
            assertEquals(trader.getParty(), order.getTrader());
            assertEquals(giveup.getParty(), order.getGiveup());
            assertEquals(book.getContr(), order.getContr());
            assertEquals(settlDay, order.getSettlDay());
            assertEquals("", order.getRef());
            assertEquals(State.NEW, order.getState());
            assertEquals(Action.BUY, order.getAction());
            assertEquals(12345, order.getTicks());
            assertEquals(1, order.getLots());
            assertEquals(1, order.getResd());
            assertEquals(0, order.getExec());
            assertEquals(0, order.getLastTicks());
            assertEquals(0, order.getLastLots());
            assertEquals(1, order.getMinLots());
            assertEquals(order.getCreated(), order.getModified());
        } finally {
            s.close();
        }
    }
}
