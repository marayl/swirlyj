/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.rest;

import static com.swirlycloud.twirly.util.JsonUtil.PARAMS_NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.swirlycloud.twirly.domain.Side;
import com.swirlycloud.twirly.domain.State;
import com.swirlycloud.twirly.entity.Order;
import com.swirlycloud.twirly.exception.BadRequestException;
import com.swirlycloud.twirly.exception.NotFoundException;
import com.swirlycloud.twirly.exception.ServiceUnavailableException;
import com.swirlycloud.twirly.node.JslNode;
import com.swirlycloud.twirly.rest.BackUnrest.ResultStruct;

public final class OrderRestTest extends RestTest {

    // Get Order.

    @Test
    public final void testGetAll() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        final Map<Long, Order> out = unrest.getOrder(MARAYL, PARAMS_NONE, TODAY_MILLIS);
        assertEquals(1, out.size());
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.get(Long.valueOf(1)));
        assertTrue(unrest.getOrder(GOSAYL, PARAMS_NONE, TODAY_MILLIS).isEmpty());
    }

    @Test
    public final void testGetByMarket() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        final Map<Long, Order> out = unrest.getOrder(MARAYL, EURUSD_MAR14, PARAMS_NONE,
                TODAY_MILLIS);
        assertEquals(1, out.size());
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.get(Long.valueOf(1)));
        assertTrue(unrest.getOrder(MARAYL, USDJPY_MAR14, PARAMS_NONE, TODAY_MILLIS).isEmpty());
    }

    @Test
    public final void testGetByMarketId() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        final Order out = unrest.getOrder(MARAYL, EURUSD_MAR14, 1, PARAMS_NONE, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0, out);
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 2, PARAMS_NONE, TODAY_MILLIS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }

    // Create Order.

    @Test
    public final void testCreate() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        final ResultStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345,
                TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 10, 1, 0, 0, 0, out.view);
    }

    // Revise Order.

    @Test
    public final void testReviseSingle() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        ResultStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 10, 1, 0, 0, 0, out.view);

        out = putOrder(MARAYL, EURUSD_MAR14, 1, 5, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.REVISE, Side.SELL, 5, 12345, 5, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 5, 1, 0, 0, 0, out.view);
    }

    @Test
    public final void testReviseBatch() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        ResultStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 10, 1, 0, 0, 0, out.view);

        out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12346, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12346, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(2)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 1, 0, 0, 0, 12346, 10, 1, 0, 0, 0, out.view);

        out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12347, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12347, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(3)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 2, 0, 0, 0, 12347, 10, 1, 0, 0, 0, out.view);

        out = putOrder(MARAYL, EURUSD_MAR14,
                jslList(EURUSD_MAR14, Long.valueOf(1), Long.valueOf(2), Long.valueOf(3)), 5,
                TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.REVISE, Side.SELL, 5, 12345, 5, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 5, 1, 0, 0, 0, out.view);

        assertOrder(MARAYL, EURUSD_MAR14, State.REVISE, Side.SELL, 5, 12346, 5, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(2)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 1, 0, 0, 0, 12346, 5, 1, 0, 0, 0, out.view);

        assertOrder(MARAYL, EURUSD_MAR14, State.REVISE, Side.SELL, 5, 12347, 5, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(3)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 2, 0, 0, 0, 12347, 5, 1, 0, 0, 0, out.view);
    }

    // Cancel Order.

    @Test
    public final void testCancelSingle() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        ResultStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 10, 1, 0, 0, 0, out.view);

        out = putOrder(MARAYL, EURUSD_MAR14, 1, 0, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12345, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, out.view);
    }

    @Test
    public final void testCancelBatch() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        ResultStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 12345, 10, 1, 0, 0, 0, out.view);

        out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12346, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12346, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(2)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 1, 0, 0, 0, 12346, 10, 1, 0, 0, 0, out.view);

        out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12347, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12347, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(3)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 2, 0, 0, 0, 12347, 10, 1, 0, 0, 0, out.view);

        out = putOrder(MARAYL, EURUSD_MAR14,
                jslList(EURUSD_MAR14, Long.valueOf(1), Long.valueOf(2), Long.valueOf(3)), 0,
                TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12345, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, out.view);

        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12346, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(2)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, out.view);

        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12347, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(3)));
        assertView(EURUSD_MAR14, EURUSD, SETTL_DAY, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, out.view);
    }

    // Archive Order.

    @Test
    public final void testArchiveSingle() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        putOrder(MARAYL, EURUSD_MAR14, 1, 0, TODAY_MILLIS);
        assertNotNull(unrest.getOrder(MARAYL, EURUSD_MAR14, 1, PARAMS_NONE, TODAY_MILLIS));
        deleteOrder(MARAYL, EURUSD_MAR14, 1, TODAY_MILLIS);
        // Order no longer exists.
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 1, PARAMS_NONE, TODAY_MILLIS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        // Duplicate operation fails.
        try {
            deleteOrder(MARAYL, EURUSD_MAR14, 1, TODAY_MILLIS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }

    @Test
    public final void testArchiveBatch() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        ResultStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12346, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12346, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(2)));
        out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12347, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.NEW, Side.SELL, 10, 12347, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(3)));
        final JslNode ids = jslList(EURUSD_MAR14, Long.valueOf(1), Long.valueOf(2),
                Long.valueOf(3));
        out = putOrder(MARAYL, EURUSD_MAR14, ids, 0, TODAY_MILLIS);
        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12345, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12346, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(2)));
        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12347, 0, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(3)));
        deleteOrder(MARAYL, EURUSD_MAR14, ids, TODAY_MILLIS);
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 1, PARAMS_NONE, TODAY_MILLIS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 2, PARAMS_NONE, TODAY_MILLIS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
        try {
            unrest.getOrder(MARAYL, EURUSD_MAR14, 3, PARAMS_NONE, TODAY_MILLIS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }
}
