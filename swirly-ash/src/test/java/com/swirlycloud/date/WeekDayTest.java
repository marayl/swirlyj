/*******************************************************************************
 * Copyright (C) 2013, 2014 Mark Aylett <mark.aylett@gmail.com>
 *
 * All rights reserved.
 *******************************************************************************/
package com.swirlycloud.date;

import static com.swirlycloud.date.DateUtil.*;
import static org.junit.Assert.*;

import org.junit.Test;

public final class WeekDayTest {
    @Test
    public final void test() {
        assertEquals(WeekDay.THU, WeekDay.valueOfJd(ymdToJd(2014, 3, 13)));
        assertEquals(WeekDay.FRI, WeekDay.valueOfJd(ymdToJd(2014, 3, 14)));
        assertEquals(WeekDay.SAT, WeekDay.valueOfJd(ymdToJd(2014, 3, 15)));
    }
}