/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.fix;

import static com.swirlycloud.swirly.date.JulianDay.jdToMs;
import static com.swirlycloud.swirly.date.JulianDay.ymdToJd;
import static com.swirlycloud.swirly.fix.FixUtility.readProperties;
import static com.swirlycloud.swirly.io.CacheUtil.NO_CACHE;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.swirlycloud.swirly.app.LockableServ;
import com.swirlycloud.swirly.domain.Side;
import com.swirlycloud.swirly.exception.AlreadyExistsException;
import com.swirlycloud.swirly.io.Datastore;
import com.swirlycloud.swirly.mock.MockDatastore;
import com.swirlycloud.swirly.quickfix.NullLogFactory;

import quickfix.FieldNotFound;
import quickfix.LogFactory;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.MsgType;

public final class FixTest {

    private static final int TODAY = ymdToJd(2014, 2, 11);
    private static final int SETTL_DAY = TODAY + 2;
    private static final int EXPIRY_DAY = TODAY + 1;

    private static final long NOW = jdToMs(TODAY);

    private FixServ fixServ;
    private FixClnt fixClnt;

    @BeforeClass
    public static void setUpClass() throws IOException {
        PropertyConfigurator.configure(readProperties("log4j.properties"));
    }

    @Before
    public final void setUp() throws Exception {
        @SuppressWarnings("resource")
        final Datastore datastore = new MockDatastore();
        final LockableServ serv = new LockableServ(datastore, datastore, NO_CACHE, NOW);
        final int lock = serv.writeLock();
        try {
            serv.createMarket("EURUSD.MAR14", "EURUSD March 14", "EURUSD", SETTL_DAY, EXPIRY_DAY, 0,
                    NOW);
        } finally {
            serv.unlock(lock);
        }

        final LogFactory logFactory = new NullLogFactory();
        final FixServ fixServ = FixServ.create("FixServ.conf", serv, logFactory);
        FixClnt fixClnt = null;

        boolean success = false;
        try {
            fixClnt = FixClnt.create("FixClnt.conf", logFactory);
            fixClnt.waitForLogon();
            success = true;
        } finally {
            if (!success) {
                try {
                    if (fixClnt != null) {
                        fixClnt.close();
                    }
                } finally {
                    fixServ.close();
                }
            }
        }
        // Commit.
        this.fixServ = fixServ;
        this.fixClnt = fixClnt;
    }

    @After
    public final void tearDown() throws Exception {
        // Assumption: MockDatastore need not be closed because it does not acquire resources.
        try {
            if (fixClnt != null) {
                fixClnt.close();
                fixClnt = null;
            }
        } finally {
            if (fixServ != null) {
                fixServ.close();
                fixServ = null;
            }
        }
    }

    @Test
    public final void test() throws FieldNotFound, AlreadyExistsException, SessionNotFound,
            ExecutionException, InterruptedException {
        final SessionID marayl = new SessionID("FIX.4.4", "MarkAylett", "Swirly");
        final SessionID gosayl = new SessionID("FIX.4.4", "GoskaAylett", "Swirly");

        final FixBuilder sb = new FixBuilder();
        sb.setMessage(fixClnt.sendNewOrderSingle("EURUSD.MAR14", "marayl1", Side.BUY, 10, 12345, 1,
                NOW + 1, marayl).get());
        assertEquals(MsgType.EXECUTION_REPORT, sb.getMsgType());

        sb.setMessage(fixClnt.sendNewOrderSingle("EURUSD.MAR14", "gosayl1", Side.SELL, 5, 12345, 1,
                NOW + 2, gosayl).get());
        assertEquals(MsgType.EXECUTION_REPORT, sb.getMsgType());

        sb.setMessage(fixClnt.sendOrderCancelReplaceRequest("EURUSD.MAR14", "marayl2", "marayl1", 9,
                NOW + 3, marayl).get());
        assertEquals(MsgType.EXECUTION_REPORT, sb.getMsgType());

        sb.setMessage(fixClnt
                .sendOrderCancelRequest("EURUSD.MAR14", "marayl3", "marayl1", NOW + 4, marayl)
                .get());
        assertEquals(MsgType.EXECUTION_REPORT, sb.getMsgType());

        sb.setMessage(fixClnt
                .sendOrderCancelRequest("EURUSD.MAR14", "marayl3", "marayl1", NOW + 5, marayl)
                .get());
        assertEquals(MsgType.ORDER_CANCEL_REJECT, sb.getMsgType());
    }
}
