/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.date;

import static com.swirlycloud.swirly.date.JulianDay.ymdToJd;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class WeekDayTest {
    @Test
    public final void test() {
        assertEquals(WeekDay.THU, WeekDay.valueOfJd(ymdToJd(2014, 2, 13)));
        assertEquals(WeekDay.FRI, WeekDay.valueOfJd(ymdToJd(2014, 2, 14)));
        assertEquals(WeekDay.SAT, WeekDay.valueOfJd(ymdToJd(2014, 2, 15)));
    }
}
