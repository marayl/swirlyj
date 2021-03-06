package com.swirlycloud.swirly.rest;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.swirlycloud.swirly.domain.RecType;
import com.swirlycloud.swirly.entity.EntitySet;
import com.swirlycloud.swirly.exception.NotFoundException;
import com.swirlycloud.swirly.exception.ServiceUnavailableException;
import com.swirlycloud.swirly.util.Params;

public @NonNullByDefault interface Rest {

    @Nullable
    String findTraderByEmail(String email) throws ServiceUnavailableException, IOException;

    void getRec(EntitySet es, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getRec(RecType recType, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getRec(RecType recType, String mnem, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getSess(String trader, EntitySet es, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getOrder(String trader, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getOrder(String trader, String market, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getOrder(String trader, String market, long id, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getTrade(String trader, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getTrade(String trader, String market, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getTrade(String trader, String market, long id, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getPosn(String trader, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getPosn(String trader, String contr, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getPosn(String trader, String contr, int settlDate, Params params, long now,
            Appendable out) throws NotFoundException, ServiceUnavailableException, IOException;

    void getQuote(String trader, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getQuote(String trader, String market, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getQuote(String trader, String market, long id, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getView(Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    void getView(String market, Params params, long now, Appendable out)
            throws NotFoundException, ServiceUnavailableException, IOException;

    long getTimeout();
}
