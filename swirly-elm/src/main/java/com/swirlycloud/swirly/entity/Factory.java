/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.entity;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.swirly.domain.AssetType;
import com.swirlycloud.swirly.domain.Role;
import com.swirlycloud.swirly.domain.Side;
import com.swirlycloud.swirly.domain.State;

public @NonNullByDefault interface Factory {

    Asset newAsset(String mnem, @Nullable String display, AssetType type);

    Contr newContr(String mnem, @Nullable String display, String asset, String ccy, int lotNumer,
            int lotDenom, int tickNumer, int tickDenom, int pipDp, long minLots, long maxLots);

    Market newMarket(String mnem, @Nullable String display, String contr, int settlDay,
            int expiryDay, int state, long lastLots, long lastTicks, long lastTime, long maxOrderId,
            long maxExecId, long maxQuoteId);

    Market newMarket(String mnem, @Nullable String display, String contr, int settlDay,
            int expiryDay, int state);

    Trader newTrader(String mnem, @Nullable String display, String email);

    Order newOrder(String trader, String market, String contr, int settlDay, long id,
            @Nullable String ref, long quoteId, State state, Side side, long lots, long ticks,
            long resd, long exec, long cost, long lastLots, long lastTicks, long minLots,
            boolean pecan, long created, long modified);

    Order newOrder(String trader, String market, String contr, int settlDay, long id,
            @Nullable String ref, long quoteId, Side side, long lots, long ticks, long minLots,
            long created);

    Exec newExec(String trader, String market, String contr, int settlDay, long id,
            @Nullable String ref, long orderId, long quoteId, State state, Side side, long lots,
            long ticks, long resd, long exec, long cost, long lastLots, long lastTicks,
            long minLots, long matchId, @Nullable Role role, @Nullable String cpty, long created);

    Exec newExec(Order order, long id, long created);

    Posn newPosn(String trader, String contr, int settlDay, long buyLots, long buyCost,
            long sellLots, long sellCost);

    Posn newPosn(String trader, String contr, int settlDay);

    Quote newQuote(String trader, String market, String contr, int settlDay, long id,
            @Nullable String ref, @Nullable Order order, Side side, long lots, long ticks,
            long created, long expiry);
}
