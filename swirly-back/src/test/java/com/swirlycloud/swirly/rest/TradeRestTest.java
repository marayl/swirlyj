/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.rest;

import static com.swirlycloud.swirly.util.JsonUtil.PARAMS_NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.swirlycloud.swirly.domain.Role;
import com.swirlycloud.swirly.domain.Side;
import com.swirlycloud.swirly.domain.State;
import com.swirlycloud.swirly.entity.Exec;
import com.swirlycloud.swirly.exception.BadRequestException;
import com.swirlycloud.swirly.exception.NotFoundException;
import com.swirlycloud.swirly.exception.ServiceUnavailableException;

public final class TradeRestTest extends RestTest {

    // Get Trade.

    @Test
    public final void testGetAll() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.BUY, 10, 12345, TODAY_MS);
        final Map<Long, Exec> out = unrest.getTrade(MARAYL, PARAMS_NONE, TODAY_MS);
        assertEquals(2, out.size());
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.SELL, 10, 12345, 0, 10, 123450, 10,
                12345, EURUSD, SETTL_DAY, Role.MAKER, MARAYL, out.get(Long.valueOf(3)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 10, 12345, 0, 10, 123450, 10, 12345,
                EURUSD, SETTL_DAY, Role.TAKER, MARAYL, out.get(Long.valueOf(4)));
    }

    @Test
    public final void testGetByMarket() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.BUY, 10, 12345, TODAY_MS);
        final Map<Long, Exec> out = unrest.getTrade(MARAYL, EURUSD_MAR14, PARAMS_NONE,
                TODAY_MS);
        assertEquals(2, out.size());
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.SELL, 10, 12345, 0, 10, 123450, 10,
                12345, EURUSD, SETTL_DAY, Role.MAKER, MARAYL, out.get(Long.valueOf(3)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 10, 12345, 0, 10, 123450, 10, 12345,
                EURUSD, SETTL_DAY, Role.TAKER, MARAYL, out.get(Long.valueOf(4)));
        assertTrue(unrest.getTrade(MARAYL, USDJPY_MAR14, PARAMS_NONE, TODAY_MS).isEmpty());
    }

    @Test
    public final void testGetByMarketId() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.BUY, 10, 12345, TODAY_MS);
        final Map<Long, Exec> out = unrest.getTrade(MARAYL, EURUSD_MAR14, PARAMS_NONE,
                TODAY_MS);
        assertEquals(2, out.size());
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.SELL, 10, 12345, 0, 10, 123450, 10,
                12345, EURUSD, SETTL_DAY, Role.MAKER, MARAYL, out.get(Long.valueOf(3)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 10, 12345, 0, 10, 123450, 10, 12345,
                EURUSD, SETTL_DAY, Role.TAKER, MARAYL, out.get(Long.valueOf(4)));
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 3, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 4, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }

    // Create Trade.

    @Test
    public final void testCreate()
            throws NotFoundException, ServiceUnavailableException, IOException {
        final Exec exec = unrest.postTrade(MARAYL, EURUSD_MAR14, "", Side.BUY, 10, 12345,
                Role.MAKER, MARAYL, PARAMS_NONE, TODAY_MS);
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 10, 12345, 0, 10, 123450, 10, 12345,
                EURUSD, SETTL_DAY, Role.MAKER, MARAYL, exec);
    }

    // Archive Market.

    @Test
    public final void testArchiveSingle() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.BUY, 10, 12345, TODAY_MS);
        final Map<Long, Exec> out = unrest.getTrade(MARAYL, PARAMS_NONE, TODAY_MS);
        assertEquals(2, out.size());
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.SELL, 10, 12345, 0, 10, 123450, 10,
                12345, EURUSD, SETTL_DAY, Role.MAKER, MARAYL, out.get(Long.valueOf(3)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 10, 12345, 0, 10, 123450, 10, 12345,
                EURUSD, SETTL_DAY, Role.TAKER, MARAYL, out.get(Long.valueOf(4)));
        deleteTrade(MARAYL, EURUSD_MAR14, 3, TODAY_MS);
        try {
            unrest.getTrade(MARAYL, EURUSD_MAR14, 3, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }

    @Test
    public final void testArchiveBatch() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12346, TODAY_MS);
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.BUY, 20, 12346, TODAY_MS);
        final Map<Long, Exec> out = unrest.getTrade(MARAYL, PARAMS_NONE, TODAY_MS);
        assertEquals(4, out.size());
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.SELL, 10, 12345, 0, 10, 123450, 10,
                12345, EURUSD, SETTL_DAY, Role.MAKER, MARAYL, out.get(Long.valueOf(4)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 20, 12346, 10, 10, 123450, 10,
                12345, EURUSD, SETTL_DAY, Role.TAKER, MARAYL, out.get(Long.valueOf(5)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.SELL, 10, 12346, 0, 10, 123460, 10,
                12346, EURUSD, SETTL_DAY, Role.MAKER, MARAYL, out.get(Long.valueOf(6)));
        assertExec(MARAYL, EURUSD_MAR14, State.TRADE, Side.BUY, 20, 12346, 0, 20, 246910, 10, 12346,
                EURUSD, SETTL_DAY, Role.TAKER, MARAYL, out.get(Long.valueOf(7)));
        deleteTrade(MARAYL, EURUSD_MAR14, jslList(EURUSD_MAR14, Long.valueOf(4), Long.valueOf(5),
                Long.valueOf(6), Long.valueOf(7)), TODAY_MS);
        try {
            unrest.getTrade(MARAYL, EURUSD_MAR14, 4, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        try {
            unrest.getTrade(MARAYL, EURUSD_MAR14, 5, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        try {
            unrest.getTrade(MARAYL, EURUSD_MAR14, 6, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        try {
            unrest.getTrade(MARAYL, EURUSD_MAR14, 7, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }
}
