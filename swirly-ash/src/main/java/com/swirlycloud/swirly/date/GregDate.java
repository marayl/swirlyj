/*******************************************************************************
 * Copyright (C) 2013, 2015 Swirly Cloud Limited. All rights reserved.
 *******************************************************************************/
package com.swirlycloud.swirly.date;

import static com.swirlycloud.swirly.date.JulianDay.jdToIso;
import static com.swirlycloud.swirly.date.JulianDay.ymdToIso;
import static com.swirlycloud.swirly.date.JulianDay.ymdToJd;

import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.jdt.annotation.NonNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Gregorian date.
 * 
 * @author Mark Aylett
 */
public final class GregDate implements Comparable<GregDate> {
    private static final int[] MDAYS = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    /**
     * Year between 1801 and 2099 inclusive.
     */
    private final int year;
    /**
     * Month between 0 and 11 inclusive.
     */
    private final int mon;
    /**
     * Day of month between 1 and 31 inclusive.
     */
    private final int mday;

    private static int cmp(int lhs, int rhs) {
        int i;
        if (lhs < rhs) {
            i = -1;
        } else if (lhs > rhs) {
            i = 1;
        } else {
            i = 0;
        }
        return i;
    }

    public GregDate(int year, int mon, int mday) {
        assert mon <= 11;
        this.year = year;
        this.mon = mon;
        this.mday = Math.min(mday, mdays(year, mon));
    }

    @Override
    public final int hashCode() {
        return toIso();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof GregDate) {
            final GregDate rhs = (GregDate) obj;
            // Test "low order" day first.
            return mday == rhs.mday && mon == rhs.mon && year == rhs.year;
        }
        return super.equals(obj);
    }

    @Override
    public final String toString() {
        return String.valueOf(ymdToIso(year, mon, mday));
    }

    @Override
    public final int compareTo(GregDate rhs) {
        int n = cmp(year, rhs.year);
        if (0 == n) {
            n = cmp(mon, rhs.mon);
            if (0 == n) {
                n = cmp(mday, rhs.mday);
            }
        }
        return n;
    }

    public final int diffDays(GregDate rhs) {
        return rhs.toJd() - toJd();
    }

    public static boolean isLeapYear(int year) {
        return year % 100 == 0 ? year % 400 == 0 : year % 4 == 0;
    }

    public final boolean isLeapYear() {
        return isLeapYear(year);
    }

    /**
     * @param year
     *            The year.
     * @param mon
     *            The month.
     * @return the number of days in month.
     */
    public static int mdays(int year, int mon) {
        return mon == 1 && isLeapYear(year) ? 29 : MDAYS[mon];
    }

    public final int mdays() {
        return mdays(year, mon);
    }

    public final GregDate addYears(int n) {
        return new GregDate(year + n, mon, mday);
    }

    public final GregDate addMonths(int n) {
        final int total = year * 12 + mon + n;
        return new GregDate(total / 12, total % 12, mday);
    }

    public final GregDate addDays(int n) {
        return valueOfJd(toJd() + n);
    }

    public final int getYear() {
        return year;
    }

    public final int getMon() {
        return mon;
    }

    public final int getMDay() {
        return mday;
    }

    public final int toIso() {
        return ymdToIso(year, mon, mday);
    }

    public final int toJd() {
        return ymdToJd(year, mon, mday);
    }

    @NonNull
    public final Calendar toCalendar(TimeZone tz) {
        final Calendar cal = Calendar.getInstance(tz);
        cal.set(year, mon, mday, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @NonNull
    public final Calendar toCalendar() {
        return toCalendar(TimeZone.getDefault());
    }

    @NonNull
    public final WeekDay toWeekDay() {
        return WeekDay.valueOfJd(toJd());
    }

    /**
     * ISO8601 to Gregorian date.
     */
    public static @NonNull GregDate valueOfIso(int iso) {
        final int year = iso / 10000;
        final int mon = (iso / 100 % 100) - 1;
        final int mday = iso % 100;
        return new GregDate(year, mon, mday);
    }

    @NonNull
    public static GregDate valueOfJd(int jd) {
        return valueOfIso(jdToIso(jd));
    }

    @NonNull
    public static GregDate valueOf(String s) {
        return valueOfIso(Integer.parseInt(s));
    }

    @NonNull
    public static GregDate valueOf(Calendar c) {
        return new GregDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @NonNull
    public static GregDate valueOf(DateTime dt) {
        return new GregDate(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth());
    }

    @NonNull
    public static GregDate valueOf(LocalDateTime dt) {
        return new GregDate(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth());
    }

    @NonNull
    public static GregDate valueOf(LocalDate d) {
        return new GregDate(d.getYear(), d.getMonthOfYear() - 1, d.getDayOfMonth());
    }
}
