/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InstantSerTest extends ModuleTestBase
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerializationAsTimestamp01Nanoseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", NO_NANOSECS_SER, value);
    }

    @Test
    public void testSerializationAsTimestamp01Milliseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "0", value);
    }

    @Test
    public void testSerializationAsTimestamp02Nanoseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "123456789.183917322", value);
    }

    @Test
    public void testSerializationAsTimestamp02Milliseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "123456789183", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        Instant date = Instant.now();
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", DecimalUtils.toDecimal(date.getEpochSecond(), date.getNano()), value);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        Instant date = Instant.now();
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", Long.toString(date.toEpochMilli()), value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        Instant date = Instant.now();
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        ObjectMapper m = newMapperBuilder()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        String value = m.writeValueAsString(date);
        assertEquals("The value is not correct.", "[\"" + Instant.class.getName() + "\",123456789.183917322]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        ObjectMapper m = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        String value = m.writeValueAsString(date);
        assertEquals("The value is not correct.", "[\"" + Instant.class.getName() + "\",123456789183]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        Instant date = Instant.now();
        ObjectMapper m = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        String value = m.writeValueAsString(date);
        assertEquals("The value is not correct.",
                "[\"" + Instant.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", value);
    }

    @Test
    public void testSerializationOriginalIssue() throws Exception
    {
        class TempClass {
            @JsonProperty("registered_at")
            @JsonFormat(shape = JsonFormat.Shape.NUMBER)
            private Instant registeredAt;      
            
            public TempClass(long seconds, int nanos) {
                this.registeredAt = Instant.ofEpochSecond(seconds, nanos);
            }
        }

        String value = MAPPER.writer()
        .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .writeValueAsString(new TempClass(1420324047, 123456));
        assertEquals("The decimals should be deprecated.", "{\"registered_at\":1420324047.000123456}", value);
    }

    @Test
    public void testSerializationSolutionByIssuer() throws Exception
    {
        class TempClass {
            @JsonProperty("registered_at")
            @JsonFormat(shape = JsonFormat.Shape.NUMBER)
            private Instant registeredAt;      
            
            public TempClass(long seconds, int nanos) {
                this.registeredAt = Instant.ofEpochSecond(seconds, nanos);
            }

            @JsonGetter("registered_at")
            public long getEpoch() {
                return registeredAt.getEpochSecond();
            }
        }

        String value = MAPPER.writer()
        .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .writeValueAsString(new TempClass(1420324047, 123456));
        assertEquals("The decimals should be deprecated.", "{\"registered_at\":1420324047}", value);
    }

    @Test
    public void testSerializationDisableTimestamps() throws Exception
    {
        class TempClass {
            @JsonProperty("registered_at")
            // Line 1
            // @JsonFormat(shape = JsonFormat.Shape.NUMBER)
            private Instant registeredAt;      
            
            public TempClass(long seconds, int nanos) {
                this.registeredAt = Instant.ofEpochSecond(seconds, nanos);
            }
        }

        String value = MAPPER.writer()
        // Line 2
        .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .writeValueAsString(new TempClass(1420324047, 123456));
        assertEquals("The decimals should be deprecated.", "{\"registered_at\":\"2015-01-03T22:27:27.000123456Z\"}", value);
    }

    @Test
    public void testSerializationDisableNanoseconds() throws Exception
    {
        class TempClass {
            @JsonProperty("registered_at")
            @JsonFormat(shape = JsonFormat.Shape.NUMBER)
            private Instant registeredAt;      
            
            public TempClass(long seconds, int nanos) {
                this.registeredAt = Instant.ofEpochSecond(seconds, nanos);
            }
        }

        String value = MAPPER.writer()
        .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .writeValueAsString(new TempClass(1420324047, 123456));
        assertEquals("The decimals should be deprecated.", "{\"registered_at\":1420324047000}", value);
    }

}
