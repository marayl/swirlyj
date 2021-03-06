/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.io;

import static com.swirlycloud.swirly.node.JslUtil.popNext;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.swirly.domain.MarketId;
import com.swirlycloud.swirly.domain.Role;
import com.swirlycloud.swirly.entity.Exec;
import com.swirlycloud.swirly.entity.Quote;
import com.swirlycloud.swirly.exception.NotFoundException;
import com.swirlycloud.swirly.node.JslNode;
import com.swirlycloud.swirly.unchecked.UncheckedIOException;

public final class JdbcDatastore extends JdbcModel implements Datastore {
    @NonNull
    private final PreparedStatement insertMarketStmt;
    @NonNull
    private final PreparedStatement insertTraderStmt;
    @NonNull
    private final PreparedStatement insertExecStmt;
    @NonNull
    private final PreparedStatement insertQuoteStmt;
    @NonNull
    private final PreparedStatement updateMarketStmt;
    @NonNull
    private final PreparedStatement updateTraderStmt;
    @NonNull
    private final PreparedStatement updateOrderStmt;
    @NonNull
    private final PreparedStatement updateExecStmt;

    public JdbcDatastore(String url, String user, String password) {
        super(url, user, password);
        PreparedStatement insertMarketStmt = null;
        PreparedStatement insertTraderStmt = null;
        PreparedStatement insertExecStmt = null;
        PreparedStatement insertQuoteStmt = null;
        PreparedStatement updateMarketStmt = null;
        PreparedStatement updateTraderStmt = null;
        PreparedStatement updateOrderStmt = null;
        PreparedStatement updateExecStmt = null;
        boolean success = false;
        try {
            try {
                insertMarketStmt = conn.prepareStatement(
                        "INSERT INTO Market_t (mnem, display, contr, settlDay, expiryDay, state) VALUES (?, ?, ?, ?, ?, ?)");
                insertTraderStmt = conn.prepareStatement(
                        "INSERT INTO Trader_t (mnem, display, email) VALUES (?, ?, ?)");
                insertExecStmt = conn.prepareStatement(
                        "INSERT INTO Exec_t (trader, market, contr, settlDay, id, ref, orderId, quoteId, stateId, sideId, lots, ticks, resd, exec, cost, lastLots, lastTicks, minLots, matchId, roleId, cpty, archive, created, modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                insertQuoteStmt = conn.prepareStatement(
                        "INSERT INTO Quote_t (market, id) VALUES (?, ?) ON DUPLICATE KEY UPDATE id = ?");
                updateMarketStmt = conn.prepareStatement(
                        "UPDATE Market_t SET display = ?, state = ? WHERE mnem = ?");
                updateTraderStmt = conn
                        .prepareStatement("UPDATE Trader_t SET display = ? WHERE mnem = ?");
                updateOrderStmt = conn.prepareStatement(
                        "UPDATE Order_t SET archive = 1, modified = ? WHERE market = ? AND id = ?");
                updateExecStmt = conn.prepareStatement(
                        "UPDATE Exec_t SET archive = 1, modified = ? WHERE market = ? AND id = ?");
                // Success.
                assert insertMarketStmt != null;
                this.insertMarketStmt = insertMarketStmt;
                assert insertTraderStmt != null;
                this.insertTraderStmt = insertTraderStmt;
                assert insertExecStmt != null;
                this.insertExecStmt = insertExecStmt;
                assert insertQuoteStmt != null;
                this.insertQuoteStmt = insertQuoteStmt;
                assert updateMarketStmt != null;
                this.updateMarketStmt = updateMarketStmt;
                assert updateTraderStmt != null;
                this.updateTraderStmt = updateTraderStmt;
                assert updateOrderStmt != null;
                this.updateOrderStmt = updateOrderStmt;
                assert updateExecStmt != null;
                this.updateExecStmt = updateExecStmt;
                success = true;
            } finally {
                if (!success) {
                    if (updateExecStmt != null) {
                        updateExecStmt.close();
                    }
                    if (updateOrderStmt != null) {
                        updateOrderStmt.close();
                    }
                    if (updateTraderStmt != null) {
                        updateTraderStmt.close();
                    }
                    if (updateMarketStmt != null) {
                        updateMarketStmt.close();
                    }
                    if (insertQuoteStmt != null) {
                        insertQuoteStmt.close();
                    }
                    if (insertExecStmt != null) {
                        insertExecStmt.close();
                    }
                    if (insertTraderStmt != null) {
                        insertTraderStmt.close();
                    }
                    if (insertMarketStmt != null) {
                        insertMarketStmt.close();
                    }
                    super.close();
                }
            }
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void close() throws SQLException {
        updateExecStmt.close();
        updateOrderStmt.close();
        updateTraderStmt.close();
        updateMarketStmt.close();
        insertQuoteStmt.close();
        insertExecStmt.close();
        insertTraderStmt.close();
        insertMarketStmt.close();
        super.close();
    }

    @Override
    public final void createMarket(@NonNull String mnem, @Nullable String display,
            @NonNull String contr, int settlDay, int expiryDay, int state) {
        try {
            int i = 1;
            setParam(insertMarketStmt, i++, mnem);
            setParam(insertMarketStmt, i++, display);
            setParam(insertMarketStmt, i++, contr);
            setNullIfZero(insertMarketStmt, i++, settlDay);
            setNullIfZero(insertMarketStmt, i++, expiryDay);
            setParam(insertMarketStmt, i++, state);
            insertMarketStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void updateMarket(@NonNull String mnem, @Nullable String display, int state) {
        try {
            int i = 1;
            setParam(updateMarketStmt, i++, display);
            setParam(updateMarketStmt, i++, state);
            setParam(updateMarketStmt, i++, mnem);
            updateMarketStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void createTrader(@NonNull String mnem, @Nullable String display,
            @NonNull String email) {
        try {
            int i = 1;
            setParam(insertTraderStmt, i++, mnem);
            setParam(insertTraderStmt, i++, display);
            setParam(insertTraderStmt, i++, email);
            insertTraderStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void updateTrader(@NonNull String mnem, @Nullable String display)
            throws NotFoundException {
        try {
            int i = 1;
            setParam(updateTraderStmt, i++, display);
            setParam(updateTraderStmt, i++, mnem);
            updateTraderStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void createExec(@NonNull Exec exec) {
        try {
            int i = 1;
            setParam(insertExecStmt, i++, exec.getTrader());
            setParam(insertExecStmt, i++, exec.getMarket());
            setParam(insertExecStmt, i++, exec.getContr());
            setNullIfZero(insertExecStmt, i++, exec.getSettlDay());
            setParam(insertExecStmt, i++, exec.getId());
            setParam(insertExecStmt, i++, exec.getRef());
            setNullIfZero(insertExecStmt, i++, exec.getOrderId());
            setNullIfZero(insertExecStmt, i++, exec.getQuoteId());
            setParam(insertExecStmt, i++, exec.getState().intValue());
            setParam(insertExecStmt, i++, exec.getSide().intValue());
            setParam(insertExecStmt, i++, exec.getLots());
            setParam(insertExecStmt, i++, exec.getTicks());
            setParam(insertExecStmt, i++, exec.getResd());
            setParam(insertExecStmt, i++, exec.getExec());
            setParam(insertExecStmt, i++, exec.getCost());
            if (exec.getLastLots() > 0) {
                setParam(insertExecStmt, i++, exec.getLastLots());
                setParam(insertExecStmt, i++, exec.getLastTicks());
            } else {
                insertExecStmt.setNull(i++, Types.INTEGER);
                insertExecStmt.setNull(i++, Types.INTEGER);
            }
            setParam(insertExecStmt, i++, exec.getMinLots());
            setNullIfZero(insertExecStmt, i++, exec.getMatchId());
            final Role role = exec.getRole();
            if (role != null) {
                setParam(insertExecStmt, i++, role.intValue());
            } else {
                insertExecStmt.setNull(i++, Types.INTEGER);
            }
            setNullIfEmpty(insertExecStmt, i++, exec.getCpty());
            setParam(insertExecStmt, i++, false);
            setParam(insertExecStmt, i++, exec.getCreated());
            setParam(insertExecStmt, i++, exec.getCreated());
            insertExecStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void createExecList(@NonNull String market, @NonNull JslNode first) {
        // The market parameter is ignored in the Jdbc implementation.
        createExecList(first);
    }

    @Override
    public final void createExecList(@NonNull JslNode first) {

        if (first.jslNext() == null) {
            // Singleton list.
            createExec((Exec) first);
            return;
        }

        JslNode node = first;
        try {
            conn.setAutoCommit(false);
            boolean success = false;
            try {
                do {
                    final Exec exec = (Exec) node;
                    assert exec != null;
                    node = popNext(node);

                    createExec(exec);
                } while (node != null);
                conn.commit();
                success = true;
            } finally {
                if (!success) {
                    conn.rollback();
                }
                conn.setAutoCommit(true);
            }
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        } finally {
            // Clear nodes to ensure no unwanted retention.
            while (node != null) {
                node = popNext(node);
            }
        }
    }

    @Override
    public final void createQuote(@NonNull Quote quote) throws NotFoundException {
        try {
            int i = 1;
            setParam(insertQuoteStmt, i++, quote.getMarket());
            setParam(insertQuoteStmt, i++, quote.getId());
            setParam(insertQuoteStmt, i++, quote.getId());
            insertQuoteStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void archiveOrder(@NonNull String market, long id, long modified) {
        try {
            int i = 1;
            setParam(updateOrderStmt, i++, modified);
            setParam(updateOrderStmt, i++, market);
            setParam(updateOrderStmt, i++, id);
            updateOrderStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void archiveOrderList(@NonNull String market, @NonNull JslNode first,
            long modified) throws NotFoundException {
        // The market parameter is ignored in the Jdbc implementation.
        archiveOrderList(first, modified);
    }

    @Override
    public final void archiveOrderList(@NonNull JslNode first, long modified)
            throws NotFoundException {

        if (first.jslNext() == null) {
            // Singleton list.
            final MarketId mid = (MarketId) first;
            archiveOrder(mid.getMarket(), mid.getId(), modified);
            return;
        }

        JslNode node = first;
        try {
            conn.setAutoCommit(false);
            boolean success = false;
            try {
                do {
                    final MarketId mid = (MarketId) node;
                    node = node.jslNext();

                    archiveOrder(mid.getMarket(), mid.getId(), modified);
                } while (node != null);
                conn.commit();
                success = true;
            } finally {
                if (!success) {
                    conn.rollback();
                }
                conn.setAutoCommit(true);
            }
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void archiveTrade(@NonNull String market, long id, long modified) {
        try {
            int i = 1;
            setParam(updateExecStmt, i++, modified);
            setParam(updateExecStmt, i++, market);
            setParam(updateExecStmt, i++, id);
            updateExecStmt.executeUpdate();
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public final void archiveTradeList(@NonNull String market, @NonNull JslNode first,
            long modified) throws NotFoundException {
        // The market parameter is ignored in the Jdbc implementation.
        archiveTradeList(first, modified);
    }

    @Override
    public final void archiveTradeList(@NonNull JslNode first, long modified)
            throws NotFoundException {

        if (first.jslNext() == null) {
            // Singleton list.
            final MarketId mid = (MarketId) first;
            archiveTrade(mid.getMarket(), mid.getId(), modified);
            return;
        }

        JslNode node = first;
        try {
            conn.setAutoCommit(false);
            boolean success = false;
            try {
                do {
                    final MarketId mid = (MarketId) node;
                    node = node.jslNext();

                    archiveTrade(mid.getMarket(), mid.getId(), modified);
                } while (node != null);
                conn.commit();
                success = true;
            } finally {
                if (!success) {
                    conn.rollback();
                }
                conn.setAutoCommit(true);
            }
        } catch (final SQLException e) {
            throw new UncheckedIOException(e);
        }
    }
}
