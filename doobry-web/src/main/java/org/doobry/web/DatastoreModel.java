/*******************************************************************************
 * Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>
 *
 * All rights reserved.
 *******************************************************************************/
package org.doobry.web;

import static org.doobry.util.AshFactory.newId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.doobry.domain.Action;
import org.doobry.domain.Exec;
import org.doobry.domain.Kind;
import org.doobry.domain.Order;
import org.doobry.domain.Posn;
import org.doobry.domain.Rec;
import org.doobry.domain.Role;
import org.doobry.domain.State;
import org.doobry.engine.Model;
import org.doobry.mock.MockAsset;
import org.doobry.mock.MockContr;
import org.doobry.mock.MockUser;
import org.doobry.util.Identifiable;
import org.doobry.util.SlNode;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public final class DatastoreModel implements Model {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private final void putOrder(Exec exec) {
        final String kind = Kind.ORDER.camelName();
        final Key key = KeyFactory.createKey(kind, exec.getOrderId());
        final Entity entity = new Entity(kind, key);
        entity.setProperty("userId", exec.getUserId());
        entity.setProperty("contrId", exec.getContrId());
        entity.setProperty("settlDay", Integer.valueOf(exec.getSettlDay()));
        entity.setProperty("ref", exec.getRef());
        entity.setProperty("state", exec.getState().name());
        entity.setProperty("action", exec.getAction().name());
        entity.setProperty("ticks", exec.getTicks());
        entity.setProperty("lots", exec.getLots());
        entity.setProperty("resd", exec.getResd());
        entity.setProperty("exec", exec.getExec());
        entity.setProperty("lastTicks", exec.getLastTicks());
        entity.setProperty("lastLots", exec.getLastLots());
        entity.setProperty("minLots", exec.getMinLots());
        entity.setProperty("created", exec.getCreated());
        entity.setProperty("modified", exec.getCreated());
        datastore.put(entity);
    }

    private final void putExec(Exec exec) {
        final String kind = Kind.EXEC.camelName();
        final Key key = KeyFactory.createKey(kind, exec.getId());
        final Entity entity = new Entity(kind, key);
        entity.setProperty("orderId", exec.getOrderId());
        entity.setProperty("userId", exec.getUserId());
        entity.setProperty("contrId", exec.getContrId());
        entity.setProperty("settlDay", Integer.valueOf(exec.getSettlDay()));
        entity.setProperty("ref", exec.getRef());
        entity.setProperty("state", exec.getState().name());
        entity.setProperty("action", exec.getAction().name());
        entity.setProperty("ticks", exec.getTicks());
        entity.setProperty("lots", exec.getLots());
        entity.setProperty("resd", exec.getResd());
        entity.setProperty("exec", exec.getExec());
        entity.setProperty("lastTicks", exec.getLastTicks());
        entity.setProperty("lastLots", exec.getLastLots());
        entity.setProperty("minLots", exec.getMinLots());
        if (exec.getState() == State.TRADE) {
            entity.setProperty("matchId", exec.getMatchId());
            entity.setProperty("role", exec.getRole().name());
            entity.setProperty("cptyId", exec.getCptyId());
        }
        entity.setProperty("created", exec.getCreated());
        entity.setProperty("modified", exec.getCreated());
        datastore.put(entity);
    }

    @Override
    public final long allocIds(Kind kind, long num) {
        final KeyRange range = datastore.allocateIds(kind.camelName(), num);
        return range.getStart().getId();
    }

    @Override
    public final void insertExecList(Exec first) {
        for (SlNode node = first; node != null; node = node.slNext()) {
            final Exec exec = (Exec) node;
            if (exec.getState() == State.NEW) {
                putOrder(exec);
            }
            putExec(exec);
        }
    }

    @Override
    public final void insertExec(Exec exec) {
        putExec(exec);
    }

    @Override
    public final void updateExec(long id, long modified) {
        // TODO Auto-generated method stub
    }

    @Override
    public final Rec readRec(Kind kind) {
        Rec first = null;
        switch (kind) {
        case ASSET:
            first = MockAsset.newAssetList();
            break;
        case CONTR:
            first = MockContr.newContrList();
            break;
        case USER:
            first = MockUser.newUserList();
            break;
        default:
            throw new IllegalArgumentException("invalid record-type");
        }
        return first;
    }

    @Override
    public final Collection<Order> readOrder() {
        final Collection<Order> c = new ArrayList<>();
        final String kind = Kind.ORDER.camelName();
        final Query q = new Query(kind);
        final PreparedQuery pq = datastore.prepare(q);
        for (final Entity entity : pq.asIterable()) {
            final long id = entity.getKey().getId();
            final Identifiable user = newId((Long) entity.getProperty("userId"));
            final Identifiable contr = newId((Long) entity.getProperty("contrId"));
            final int settlDay = ((Long) entity.getProperty("settlDay")).intValue();
            final String ref = (String) entity.getProperty("ref");
            final State state = State.valueOf((String) entity.getProperty("state"));
            final Action action = Action.valueOf((String) entity.getProperty("action"));
            final long ticks = (Long) entity.getProperty("ticks");
            final long lots = (Long) entity.getProperty("lots");
            final long resd = (Long) entity.getProperty("resd");
            final long exec = (Long) entity.getProperty("exec");
            final long lastTicks = (Long) entity.getProperty("lastTicks");
            final long lastLots = (Long) entity.getProperty("lastLots");
            final long minLots = (Long) entity.getProperty("minLots");
            final long created = (Long) entity.getProperty("created");
            final long modified = (Long) entity.getProperty("modified");
            final Order order = new Order(id, user, contr, settlDay, ref, state, action, ticks,
                    lots, resd, exec, lastTicks, lastLots, minLots, created, modified);
            c.add(order);
        }
        return c;
    }

    @Override
    public final Collection<Exec> readTrade() {
        final Collection<Exec> c = new ArrayList<>();
        final String kind = Kind.EXEC.camelName();
        final Query q = new Query(kind);
        final PreparedQuery pq = datastore.prepare(q);
        for (final Entity entity : pq.asIterable()) {
            final long id = entity.getKey().getId();
            final long orderId = (Long) entity.getProperty("orderId");
            final Identifiable user = newId((Long) entity.getProperty("userId"));
            final Identifiable contr = newId((Long) entity.getProperty("contrId"));
            final int settlDay = ((Long) entity.getProperty("settlDay")).intValue();
            final String ref = (String) entity.getProperty("ref");
            final State state = State.valueOf((String) entity.getProperty("state"));
            final Action action = Action.valueOf((String) entity.getProperty("action"));
            final long ticks = (Long) entity.getProperty("ticks");
            final long lots = (Long) entity.getProperty("lots");
            final long resd = (Long) entity.getProperty("resd");
            final long exec = (Long) entity.getProperty("exec");
            final long lastTicks = (Long) entity.getProperty("lastTicks");
            final long lastLots = (Long) entity.getProperty("lastLots");
            final long minLots = (Long) entity.getProperty("minLots");
            long matchId;
            Role role;
            Identifiable cpty;
            if (state == State.TRADE) {
                matchId = (Long) entity.getProperty("matchId");
                role = Role.valueOf((String) entity.getProperty("role"));
                cpty = newId((Long) entity.getProperty("cptyId"));
            } else {
                matchId = 0;
                role = null;
                cpty = null;
            }
            final long created = (Long) entity.getProperty("created");

            c.add(new Exec(id, orderId, user, contr, settlDay, ref, state, action, ticks, lots,
                    resd, exec, lastTicks, lastLots, minLots, matchId, role, cpty, created));
        }
        return c;
    }

    @Override
    public final Collection<Posn> readPosn() {
        return Collections.emptyList();
    }
}