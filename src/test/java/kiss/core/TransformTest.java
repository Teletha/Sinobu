/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import kiss.I;

import org.junit.Test;

/**
 * @version 2014/03/11 2:15:34
 */
public class TransformTest {

    @Test
    public void inputNull() throws Exception {
        assert I.transform(null, int.class) == null;
        assert I.transform(null, String.class) == null;
    }

    @Test(expected = NullPointerException.class)
    public void outputNull() throws Exception {
        assert I.transform("1", null) == null;
    }

    @Test
    public void primitiveInt() {
        assert I.transform("1", int.class) == 1;
        assert I.transform(1, String.class).equals("1");
    }

    @Test
    public void primitiveLong() {
        assert I.transform("1", long.class) == 1L;
        assert I.transform(1L, String.class).equals("1");
    }

    @Test
    public void primitiveChar() {
        assert I.transform("1", char.class) == '1';
        assert I.transform('1', String.class).equals("1");
    }

    @Test
    public void primitiveFloat() {
        assert I.transform("1.3", float.class) == 1.3f;
        assert I.transform(1.3f, String.class).equals("1.3");
    }

    @Test
    public void primitiveDouble() {
        assert I.transform("1.3", double.class) == 1.3d;
        assert I.transform(1.3d, String.class).equals("1.3");
    }

    @Test
    public void primitiveBoolean() {
        assert I.transform("true", boolean.class);
        assert I.transform(true, String.class).equals("true");
    }

    @Test
    public void transformBean() {
        BeanA a = I.make(BeanA.class);
        a.setValue(10);

        // initial value
        assert 10 == a.getValue();

        // transform
        BeanB b = I.transform(a, BeanB.class);

        // initial value
        assert 10 == a.getValue();
        assert b.getValue().equals("10");
    }

    @Test
    public void date() throws Exception {
        assert I.transform(new Date(0), String.class).equals("1970-01-01T09:00:00");
        assert I.transform("1970-01-01T09:00:00", Date.class).equals(new Date(0));
    }

    @Test
    public void localDateTime() throws Exception {
        LocalDateTime local = LocalDateTime.of(2014, 3, 10, 13, 43, 56);
        String text = "2014-03-10T13:43:56";

        assert I.transform(local, String.class).equals(text);
        assert I.transform(text, LocalDateTime.class).equals(local);
    }

    @Test
    public void localDate() throws Exception {
        LocalDate local = LocalDate.of(2014, 3, 10);
        String text = "2014-03-10";

        assert I.transform(local, String.class).equals(text);
        assert I.transform(text, LocalDate.class).equals(local);
    }

    @Test
    public void localTime() throws Exception {
        LocalTime local = LocalTime.of(23, 45, 9, 765);
        String text = "23:45:09.000000765";

        assert I.transform(local, String.class).equals(text);
        assert I.transform(text, LocalTime.class).equals(local);
    }

    @Test
    public void offsetDateTime() throws Exception {
        OffsetDateTime offset = OffsetDateTime.of(2014, 3, 10, 13, 43, 56, 203, ZoneOffset.ofHours(9));
        String text = "2014-03-10T13:43:56.000000203+09:00";

        assert I.transform(offset, String.class).equals(text);
        assert I.transform(text, OffsetDateTime.class).equals(offset);
    }

    @Test
    public void offsetTime() throws Exception {
        OffsetTime offset = OffsetTime.of(13, 43, 56, 203, ZoneOffset.ofHours(9));
        String text = "13:43:56.000000203+09:00";

        assert I.transform(offset, String.class).equals(text);
        assert I.transform(text, OffsetTime.class).equals(offset);
    }

    @Test
    public void zonedDateTime() throws Exception {
        ZonedDateTime offset = ZonedDateTime.of(2014, 3, 10, 13, 43, 56, 203, ZoneId.of(ZoneId.SHORT_IDS.get("JST")));
        String text = "2014-03-10T13:43:56.000000203+09:00[Asia/Tokyo]";

        assert I.transform(offset, String.class).equals(text);
        assert I.transform(text, ZonedDateTime.class).equals(offset);
    }

    @Test
    public void monthDay() throws Exception {
        MonthDay date = MonthDay.of(10, 31);
        String text = "--10-31";

        assert I.transform(date, String.class).equals(text);
        assert I.transform(text, MonthDay.class).equals(date);
    }

    @Test
    public void yearMonth() throws Exception {
        YearMonth date = YearMonth.of(2014, 5);
        String text = "2014-05";

        assert I.transform(date, String.class).equals(text);
        assert I.transform(text, YearMonth.class).equals(date);
    }

    @Test
    public void year() throws Exception {
        Year date = Year.of(2014);
        String text = "2014";

        assert I.transform(date, String.class).equals(text);
        assert I.transform(text, Year.class).equals(date);
    }

    @Test
    public void duration() throws Exception {
        Duration duration = Duration.of(40, ChronoUnit.SECONDS);
        String text = "PT40S";

        assert I.transform(duration, String.class).equals(text);
        assert I.transform(text, Duration.class).equals(duration);
    }

    @Test
    public void period() throws Exception {
        Period period = Period.of(1, 2, 14);
        String text = "P1Y2M14D";

        assert I.transform(period, String.class).equals(text);
        assert I.transform(text, Period.class).equals(period);
    }

    @Test
    public void instant() throws Exception {
        Instant instant = Instant.ofEpochMilli(1000);
        String text = "1970-01-01T00:00:01Z";

        assert I.transform(instant, String.class).equals(text);
        assert I.transform(text, Instant.class).equals(instant);
    }

    @Test
    public void url() throws Exception {
        URL value = new URL("http://localhost:8888/");
        String text = "http://localhost:8888/";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, URL.class).equals(value);
    }

    @Test
    public void uri() throws Exception {
        URI value = new URI("http://localhost:8888/");
        String text = "http://localhost:8888/";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, URI.class).equals(value);
    }

    @Test
    public void locale() throws Exception {
        Locale value = new Locale("en");
        String text = "en";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, Locale.class).equals(value);
    }

    @Test
    public void bigInteger() throws Exception {
        BigInteger value = new BigInteger("12345678901234567890");
        String text = "12345678901234567890";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, BigInteger.class).equals(value);
    }

    @Test
    public void bigDecimal() throws Exception {
        BigDecimal value = new BigDecimal("123.456789012345678901");
        String text = "123.456789012345678901";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, BigDecimal.class).equals(value);
    }

    @Test
    public void stringBuilder() throws Exception {
        StringBuilder value = new StringBuilder("123.456789012345678901");
        String text = "123.456789012345678901";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, StringBuilder.class).toString().equals(value.toString());
    }

    @Test
    public void stringBuffer() throws Exception {
        StringBuffer value = new StringBuffer("123.456789012345678901");
        String text = "123.456789012345678901";

        assert I.transform(value, String.class).equals(text);
        assert I.transform(text, StringBuffer.class).toString().equals(value.toString());
    }

    /**
     * @version 2011/03/15 15:09:16
     */
    protected static class BeanA {

        /** The property. */
        private int value;

        /**
         * Get the value property of this {@link TransformTest.BeanA}.
         * 
         * @return The value property.
         */
        public int getValue() {
            return value;
        }

        /**
         * Set the value property of this {@link TransformTest.BeanA}.
         * 
         * @param value The value value to set.
         */
        public void setValue(int value) {
            this.value = value;
        }
    }

    /**
     * @version 2011/03/15 15:09:20
     */
    protected static class BeanB {

        /** The property. */
        private String value;

        /**
         * Get the value property of this {@link TransformTest.BeanB}.
         * 
         * @return The value property.
         */
        public String getValue() {
            return value;
        }

        /**
         * Set the value property of this {@link TransformTest.BeanB}.
         * 
         * @param value The value value to set.
         */
        public void setValue(String value) {
            this.value = value;
        }
    }
}
