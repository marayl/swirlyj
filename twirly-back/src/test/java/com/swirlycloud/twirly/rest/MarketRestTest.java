/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.rest;

import static com.swirlycloud.twirly.util.JsonUtil.PARAMS_NONE;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.swirlycloud.twirly.exception.BadRequestException;
import com.swirlycloud.twirly.exception.NotFoundException;
import com.swirlycloud.twirly.exception.ServiceUnavailableException;
import com.swirlycloud.twirly.rec.Market;
import com.swirlycloud.twirly.rec.RecType;

public final class MarketRestTest extends RestTest {

    @Test
    public final void testPostMarket() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        final Market market = postMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", 0x1);
        assertMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", 0, 0, 0x1, market);
        assertMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", 0, 0, 0x1,
                (Market) unrest.getRec(RecType.MARKET, "GBPUSD.MAR14", PARAMS_NONE, NOW));
    }

    @Test
    public final void testPostMarketSettl() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        final Market market = postMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", SETTL_DAY,
                EXPIRY_DAY, 0x1);
        assertMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", SETTL_DAY, EXPIRY_DAY, 0x1,
                market);
        assertMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", SETTL_DAY, EXPIRY_DAY, 0x1,
                (Market) unrest.getRec(RecType.MARKET, "GBPUSD.MAR14", PARAMS_NONE, NOW));
    }

    @Test
    public final void testPutMarket() throws BadRequestException, NotFoundException,
            ServiceUnavailableException, IOException {
        Market market = postMarket("GBPUSD.MAR14", "GBPUSD March 14", "GBPUSD", SETTL_DAY,
                EXPIRY_DAY, 0x1);
        market = putMarket("MARAYL", "GBPUSD.MAR14", "GBPUSD March 14x", 0x2);
        assertMarket("GBPUSD.MAR14", "GBPUSD March 14x", "GBPUSD", SETTL_DAY, EXPIRY_DAY, 0x2,
                market);
        assertMarket("GBPUSD.MAR14", "GBPUSD March 14x", "GBPUSD", SETTL_DAY, EXPIRY_DAY, 0x2,
                (Market) unrest.getRec(RecType.MARKET, "GBPUSD.MAR14", PARAMS_NONE, NOW));
    }

    @Ignore("not implemented")
    @Test
    public final void testGetEndOfDay() {
        // FIXME
    }
}