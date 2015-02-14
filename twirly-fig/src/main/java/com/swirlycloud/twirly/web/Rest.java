/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.twirly.web;

import static com.swirlycloud.twirly.app.DateUtil.getBusDate;
import static com.swirlycloud.twirly.date.JulianDay.isoToJd;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import com.swirlycloud.twirly.app.Model;
import com.swirlycloud.twirly.app.Serv;
import com.swirlycloud.twirly.app.Sess;
import com.swirlycloud.twirly.app.Trans;
import com.swirlycloud.twirly.domain.Action;
import com.swirlycloud.twirly.domain.Contr;
import com.swirlycloud.twirly.domain.Exec;
import com.swirlycloud.twirly.domain.Market;
import com.swirlycloud.twirly.domain.Order;
import com.swirlycloud.twirly.domain.Posn;
import com.swirlycloud.twirly.domain.Rec;
import com.swirlycloud.twirly.domain.RecType;
import com.swirlycloud.twirly.domain.Trader;
import com.swirlycloud.twirly.exception.BadRequestException;
import com.swirlycloud.twirly.exception.NotFoundException;
import com.swirlycloud.twirly.node.RbNode;
import com.swirlycloud.twirly.util.Params;

public final class Rest {

    private static final int PERMITS = Runtime.getRuntime().availableProcessors();
    private final Semaphore sem = new Semaphore(PERMITS);
    private final Serv serv;

    private final void acquireRead() {
        sem.acquireUninterruptibly(1);
    }

    private final void releaseRead() {
        sem.release(1);
    }

    private final void acquireWrite() {
        sem.acquireUninterruptibly(PERMITS);
    }

    private final void releaseWrite() {
        sem.release(PERMITS);
    }

    private final boolean getExpiredParam(Params params) {
        final Boolean val = params.getParam("expired", Boolean.class);
        return val == null ? false : val.booleanValue();
    }

    private final void doGetRec(RecType recType, Params params, long now, Appendable out)
            throws IOException {
        out.append('[');
        RbNode node = serv.getFirstRec(recType);
        for (int i = 0; node != null; node = node.rbNext()) {
            final Rec rec = (Rec) node;
            if (i > 0) {
                out.append(',');
            }
            rec.toJson(params, out);
            ++i;
        }
        out.append(']');
    }

    private final void doGetOrder(Sess sess, Params params, long now, Appendable out)
            throws IOException {
        out.append('[');
        RbNode node = sess.getFirstOrder();
        for (int i = 0; node != null; node = node.rbNext()) {
            final Order order = (Order) node;
            if (i > 0) {
                out.append(',');
            }
            order.toJson(params, out);
            ++i;
        }
        out.append(']');
    }

    private final void doGetTrade(Sess sess, Params params, long now, Appendable out)
            throws IOException {
        out.append('[');
        RbNode node = sess.getFirstTrade();
        for (int i = 0; node != null; node = node.rbNext()) {
            final Exec trade = (Exec) node;
            if (i > 0) {
                out.append(',');
            }
            trade.toJson(params, out);
            ++i;
        }
        out.append(']');
    }

    private final void doGetPosn(Sess sess, Params params, long now, Appendable out)
            throws IOException {
        out.append('[');
        RbNode node = sess.getFirstPosn();
        for (int i = 0; node != null; node = node.rbNext()) {
            final Posn posn = (Posn) node;
            if (i > 0) {
                out.append(',');
            }
            posn.toJson(params, out);
            ++i;
        }
        out.append(']');
    }

    public Rest(Model model) {
        serv = new Serv(model);
    }

    public final void getRec(boolean withTraders, Params params, long now, Appendable out)
            throws IOException {
        acquireRead();
        try {
            out.append("{\"assets\":");
            doGetRec(RecType.ASSET, params, now, out);
            out.append(",\"contrs\":");
            doGetRec(RecType.CONTR, params, now, out);
            out.append(",\"markets\":");
            doGetRec(RecType.MARKET, params, now, out);
            if (withTraders) {
                out.append(",\"traders\":");
                doGetRec(RecType.TRADER, params, now, out);
            }
            out.append('}');
        } finally {
            releaseRead();
        }
    }

    public final void getRec(RecType recType, Params params, long now, Appendable out)
            throws IOException {
        acquireRead();
        try {
            doGetRec(recType, params, now, out);
        } finally {
            releaseRead();
        }
    }

    public final void getRec(RecType recType, String mnem, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Rec rec = serv.findRec(recType, mnem);
            if (rec == null) {
                throw new NotFoundException(String.format("record '%s' does not exist", mnem));
            }
            rec.toJson(params, out);
        } finally {
            releaseRead();
        }
    }

    public final void postTrader(String mnem, String display, String email, Params params,
            long now, Appendable out) throws BadRequestException, IOException {
        acquireWrite();
        try {
            final Trader trader = serv.createTrader(mnem, display, email);
            trader.toJson(params, out);
        } finally {
            releaseWrite();
        }
    }

    public final void postMarket(String mnem, String display, String contrMnem, int settlDate,
            int expiryDate, Params params, long now, Appendable out) throws BadRequestException,
            NotFoundException, IOException {
        acquireWrite();
        try {
            final Contr contr = (Contr) serv.findRec(RecType.CONTR, contrMnem);
            if (contr == null) {
                throw new NotFoundException(String.format("contract '%s' does not exist", contr));
            }
            final int settlDay = isoToJd(settlDate);
            final int expiryDay = isoToJd(expiryDate);
            final Market market = serv.createMarket(mnem, display, contr, settlDay, expiryDay, now);
            market.toJson(params, out);
        } finally {
            releaseWrite();
        }
    }

    public final void getView(Params params, long now, Appendable out) throws IOException {
        acquireRead();
        try {
            final boolean withExpired = getExpiredParam(params);
            final int busDay = getBusDate(now).toJd();
            out.append('[');
            RbNode node = serv.getFirstRec(RecType.MARKET);
            for (int i = 0; node != null; node = node.rbNext()) {
                final Market market = (Market) node;
                if (!withExpired && market.getExpiryDay() < busDay) {
                    // Ignore expired contracts.
                    continue;
                }
                if (i > 0) {
                    out.append(',');
                }
                market.toJsonView(params, out);
                ++i;
            }
            out.append(']');
        } finally {
            releaseRead();
        }
    }

    public final void getView(String marketMnem, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final boolean withExpired = getExpiredParam(params);
            final int busDay = getBusDate(now).toJd();
            final Market market = (Market) serv.findRec(RecType.MARKET, marketMnem);
            if (market == null) {
                throw new NotFoundException(String.format("market '%s' does not exist", market));
            }
            if (!withExpired && market.getExpiryDay() < busDay) {
                throw new NotFoundException(String.format("market '%s' has expired", market));
            }
            market.toJsonView(params, out);
        } finally {
            releaseRead();
        }
    }

    public final void getSess(String email, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Trader trader = serv.findTraderByEmail(email);
            if (trader == null) {
                throw new NotFoundException(String.format("trader '%s' does not exist", email));
            }
            final Sess sess = serv.findSess(trader.getMnem());
            if (sess == null) {
                out.append("{\"orders\":[],\"trades\":[],\"posns\":[]}");
                return;
            }
            out.append("{\"orders\":");
            doGetOrder(sess, params, now, out);
            out.append(",\"trades\":");
            doGetTrade(sess, params, now, out);
            out.append(",\"posns\":");
            doGetPosn(sess, params, now, out);
            out.append('}');
        } finally {
            releaseRead();
        }
    }

    public final void deleteOrder(String email, String market, long id, long now)
            throws BadRequestException, NotFoundException, IOException {
        acquireWrite();
        try {
            final Sess sess = serv.findSessByEmail(email);
            if (sess == null) {
                throw new NotFoundException(String.format("trader '%s' has no orders", email));
            }
            serv.archiveOrder(sess, market, id, now);
        } finally {
            releaseWrite();
        }
    }

    public final void getOrder(String email, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Trader trader = serv.findTraderByEmail(email);
            if (trader == null) {
                throw new NotFoundException(String.format("trader '%s' does not exist", email));
            }
            final Sess sess = serv.findSess(trader.getMnem());
            if (sess == null) {
                out.append("[]");
                return;
            }
            doGetOrder(sess, params, now, out);
        } finally {
            releaseRead();
        }
    }

    public final void getOrder(String email, String market, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Trader trader = serv.findTraderByEmail(email);
            if (trader == null) {
                throw new NotFoundException(String.format("trader '%s' does not exist", email));
            }
            final Sess sess = serv.findSess(trader.getMnem());
            if (sess == null) {
                out.append("[]");
                return;
            }
            out.append('[');
            RbNode node = sess.getFirstOrder();
            for (int i = 0; node != null; node = node.rbNext()) {
                final Order order = (Order) node;
                if (!order.getMarket().equals(market)) {
                    continue;
                }
                if (i > 0) {
                    out.append(',');
                }
                order.toJson(params, out);
                ++i;
            }
            out.append(']');
        } finally {
            releaseRead();
        }
    }

    public final void getOrder(String email, String market, long id, Params params, long now,
            Appendable out) throws IOException, NotFoundException {
        acquireRead();
        try {
            final Sess sess = serv.findSessByEmail(email);
            if (sess == null) {
                throw new NotFoundException(String.format("trader '%s' has no orders", email));
            }
            final Order order = sess.findOrder(market, id);
            if (order == null) {
                throw new NotFoundException(String.format("order '%d' does not exist", id));
            }
            order.toJson(params, out);
        } finally {
            releaseRead();
        }
    }

    public final void postOrder(String email, String marketMnem, String ref, Action action,
            long ticks, long lots, long minLots, Params params, long now, Appendable out)
            throws BadRequestException, NotFoundException, IOException {
        acquireWrite();
        try {
            final Sess sess = serv.getLazySessByEmail(email);
            final Market market = (Market) serv.findRec(RecType.MARKET, marketMnem);
            if (market == null) {
                throw new NotFoundException(String.format("market '%s' does not exist", market));
            }
            final Trans trans = serv.placeOrder(sess, market, ref, action, ticks, lots, minLots,
                    now, new Trans());
            trans.toJson(params, out);
        } finally {
            releaseWrite();
        }
    }

    public final void putOrder(String email, String marketMnem, long id, long lots, Params params,
            long now, Appendable out) throws BadRequestException, NotFoundException, IOException {
        acquireWrite();
        try {
            final Sess sess = serv.findSessByEmail(email);
            if (sess == null) {
                throw new NotFoundException(String.format("trader '%s' has no orders", email));
            }
            final Market market = (Market) serv.findRec(RecType.MARKET, marketMnem);
            if (market == null) {
                throw new NotFoundException(String.format("market '%s' does not exist", market));
            }
            final Trans trans = new Trans();
            if (lots > 0) {
                serv.reviseOrder(sess, market, id, lots, now, trans);
            } else {
                serv.cancelOrder(sess, market, id, now, trans);
            }
            trans.toJson(params, out);
        } finally {
            releaseWrite();
        }
    }

    public final void deleteTrade(String email, String market, long id, long now)
            throws BadRequestException, NotFoundException {
        acquireWrite();
        try {
            final Sess sess = serv.findSessByEmail(email);
            if (sess == null) {
                throw new NotFoundException(String.format("trader '%s' has no trades", email));
            }
            serv.archiveTrade(sess, market, id, now);
        } finally {
            releaseWrite();
        }
    }

    public final void getTrade(String email, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Trader trader = serv.findTraderByEmail(email);
            if (trader == null) {
                throw new NotFoundException(String.format("trader '%s' does not exist", email));
            }
            final Sess sess = serv.findSess(trader.getMnem());
            if (sess == null) {
                out.append("[]");
                return;
            }
            doGetTrade(sess, params, now, out);
        } finally {
            releaseRead();
        }
    }

    public final void getTrade(String email, String market, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Trader trader = serv.findTraderByEmail(email);
            if (trader == null) {
                throw new NotFoundException(String.format("trader '%s' does not exist", email));
            }
            final Sess sess = serv.findSess(trader.getMnem());
            if (sess == null) {
                out.append("[]");
                return;
            }
            out.append('[');
            RbNode node = sess.getFirstTrade();
            for (int i = 0; node != null; node = node.rbNext()) {
                final Exec trade = (Exec) node;
                if (!trade.getMarket().equals(market)) {
                    continue;
                }
                if (i > 0) {
                    out.append(',');
                }
                trade.toJson(params, out);
                ++i;
            }
            out.append(']');
        } finally {
            releaseRead();
        }
    }

    public final void getTrade(String email, String market, long id, Params params, long now,
            Appendable out) throws NotFoundException, IOException {
        acquireRead();
        try {
            final Sess sess = serv.findSessByEmail(email);
            if (sess == null) {
                throw new NotFoundException(String.format("trader '%s' has no trades", email));
            }
            final Exec trade = sess.findTrade(market, id);
            if (trade == null) {
                throw new NotFoundException(String.format("trade '%d' does not exist", id));
            }
            trade.toJson(params, out);
        } finally {
            releaseRead();
        }
    }

    public final void getPosn(String email, Params params, long now, Appendable out)
            throws NotFoundException, IOException {
        acquireRead();
        try {
            final Trader trader = serv.findTraderByEmail(email);
            if (trader == null) {
                throw new NotFoundException(String.format("trader '%s' does not exist", email));
            }
            final Sess sess = serv.findSess(trader.getMnem());
            if (sess == null) {
                out.append("[]");
                return;
            }
            doGetPosn(sess, params, now, out);
        } finally {
            releaseRead();
        }
    }

    public final void getPosn(String email, String contr, int settlDate, Params params, long now,
            Appendable out) throws NotFoundException, IOException {
        acquireRead();
        try {
            final Sess sess = serv.findSessByEmail(email);
            if (sess == null) {
                throw new NotFoundException(String.format("trader '%s' has no posns", email));
            }
            final Posn posn = sess.findPosn(contr, isoToJd(settlDate));
            if (posn == null) {
                throw new NotFoundException(String.format("posn for '%s' on '%d' does not exist",
                        contr, settlDate));
            }
            posn.toJson(params, out);
        } finally {
            releaseRead();
        }
    }

    // Cron jobs.

    public final void getEndOfDay(long now) throws NotFoundException {
        acquireWrite();
        try {
            serv.expireMarkets(now);
            serv.settlMarkets(now);
        } finally {
            releaseWrite();
        }
    }
}
