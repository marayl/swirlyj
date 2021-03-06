/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.rest;

import static com.swirlycloud.swirly.util.JsonUtil.PARAMS_NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.swirlycloud.swirly.domain.Side;
import com.swirlycloud.swirly.domain.State;
import com.swirlycloud.swirly.entity.MarketView;
import com.swirlycloud.swirly.entity.Order;
import com.swirlycloud.swirly.entity.Quote;
import com.swirlycloud.swirly.exception.BadRequestException;
import com.swirlycloud.swirly.exception.InternalException;
import com.swirlycloud.swirly.exception.LiquidityUnavailableException;
import com.swirlycloud.swirly.exception.NotFoundException;
import com.swirlycloud.swirly.exception.ServiceUnavailableException;
import com.swirlycloud.swirly.rest.BackUnrest.ResponseStruct;

public final class QuoteRestTest extends RestTest {

    // Get Quote.

    @Test
    public final void testGetAll() throws BadRequestException, InternalException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        final Map<Long, Quote> out = unrest.getQuote(MARAYL, PARAMS_NONE, TODAY_MS);
        assertEquals(1, out.size());
        assertQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, 12345, out.get(Long.valueOf(1)));
        assertTrue(unrest.getQuote(GOSAYL, PARAMS_NONE, TODAY_MS).isEmpty());
    }

    @Test
    public final void testGetByMarket() throws BadRequestException, InternalException,
            NotFoundException, ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        final Map<Long, Quote> out = unrest.getQuote(MARAYL, EURUSD_MAR14, PARAMS_NONE,
                TODAY_MS);
        assertEquals(1, out.size());
        assertQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, 12345, out.get(Long.valueOf(1)));
        assertTrue(unrest.getQuote(MARAYL, USDJPY_MAR14, PARAMS_NONE, TODAY_MS).isEmpty());
    }

    @Test
    public final void testGetByMarketId() throws BadRequestException, InternalException,
            NotFoundException, ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        final Quote out = unrest.getQuote(MARAYL, EURUSD_MAR14, 1, PARAMS_NONE, TODAY_MS);
        assertQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, 12345, out);
        try {
            unrest.getQuote(MARAYL, EURUSD_MAR14, 2, PARAMS_NONE, TODAY_MS);
            fail("Expected exception");
        } catch (final NotFoundException e) {
        }
    }

    // Create Quote.

    @Test
    public final void testCreate() throws BadRequestException, InternalException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        final Quote quote = postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        assertQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, 12345, quote);
    }

    @Test
    public final void testExpiry() throws BadRequestException, InternalException, NotFoundException,
            ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        final Map<Long, Quote> out = unrest.getQuote("MARAYL", PARAMS_NONE,
                TODAY_MS + QUOTE_EXPIRY);
        assertTrue(out.isEmpty());
    }

    @Test(expected = LiquidityUnavailableException.class)
    public final void testNoLiquidity() throws BadRequestException, InternalException,
            NotFoundException, ServiceUnavailableException, IOException {
        postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 1, TODAY_MS);
    }

    @Test
    public final void testPecan() throws BadRequestException, InternalException, NotFoundException,
            ServiceUnavailableException, IOException {
        ResponseStruct out = postOrder(MARAYL, EURUSD_MAR14, 0, Side.SELL, 10, 12345, TODAY_MS);
        MarketView view = out.view;
        assert view != null;
        assertTrue(view.isValidOffer(0));

        final Quote quote = postQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, TODAY_MS);
        assertQuote(MARAYL, EURUSD_MAR14, Side.BUY, 10, 12345, quote);

        out = putOrder(MARAYL, EURUSD_MAR14, 1, 0, TODAY_MS);
        assertOrder(MARAYL, EURUSD_MAR14, State.PECAN, Side.SELL, 10, 12345, 10, 0, 0, 0, 0,
                out.orders.get(Long.valueOf(1)));
        view = out.view;
        assert view != null;
        assertFalse(view.isValidOffer(0));

        final Order order = unrest.getOrder(MARAYL, EURUSD_MAR14, 1, PARAMS_NONE,
                TODAY_MS + QUOTE_EXPIRY);
        assertOrder(MARAYL, EURUSD_MAR14, State.CANCEL, Side.SELL, 10, 12345, 0, 0, 0, 0, 0,
                TODAY_MS, TODAY_MS + QUOTE_EXPIRY, order);
    }
}
