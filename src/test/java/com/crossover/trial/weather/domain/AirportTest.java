/**
 * The MIT License
 * Copyright (c) 2016 Thiago Souza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.crossover.trial.weather.domain;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static com.crossover.trial.weather.domain.IATA.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class AirportTest {

    @Test
    public void testSimple() {
        Airport airport = new Airport.Builder()
                .withIata(valueOf("AAA"))
                .withLatitude(10)
                .withLongitude(20)
                .build();

        assertThat(airport.getIata(), equalTo(valueOf("AAA")));
        assertThat(airport.getLatitude(), equalTo(10D));
        assertThat(airport.getLongitude(), equalTo(20D));
    }


    @Test
    public void testEquality() {
        Airport airport1 = new Airport.Builder()
                .withIata(valueOf("AAA"))
                .withLatitude(10)
                .withLongitude(20)
                .build();

        Airport airport2 = new Airport.Builder()
                .withIata(valueOf("aaa"))
                .withLatitude(10)
                .withLongitude(20)
                .build();

        assertThat(airport1, equalTo(airport2));
        assertThat(airport1.hashCode(), equalTo(airport2.hashCode()));

    }

    @Test
    public void testCalculateDistance() {
        Airport jfk = new Airport.Builder()
                .withIata(valueOf("JFK"))
                .withLatitude(40.639751)
                .withLongitude(-73.778925)
                .build();

        Airport gig = new Airport.Builder()
                .withIata(valueOf("GIG"))
                .withLatitude(-22.808903)
                .withLongitude(-43.243647)
                .build();

        assertThat(jfk.calculateDistance(gig),
                equalTo(gig.calculateDistance(jfk)));
        assertThat(gig.calculateDistance(jfk),
                equalTo(jfk.calculateDistance(gig)));

        assertThat(gig.calculateDistance(jfk),
                equalTo(7703.945145479768));

        assertThat(gig.calculateDistance(gig),
                equalTo(0D));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        new Airport.Builder().build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_1() {
        new Airport.Builder().withIata("aa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_2() {
        new Airport.Builder().withIata("ab1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_3() {
        new Airport.Builder().withIata("abc1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_4() {
        new Airport.Builder().withIata("AbcD");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_5() {
        new Airport.Builder().withIata("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_6() {
        new Airport.Builder().withIata("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIANA_7() {
        new Airport.Builder().withIata((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLatitude() {
        new Airport.Builder().withLatitude(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLongitude() {
        new Airport.Builder().withLatitude(-1000);
    }

    @Test
    public void testSerialization() throws IOException {
        JsonFactory jsonFactory = new MappingJsonFactory();
        Airport airport = new Airport.Builder()
                .withIata("aaa")
                .withLatitude(10)
                .withLongitude(20)
                .build();

        StringWriter out = new StringWriter();
        JsonGenerator json = jsonFactory.createGenerator(out);
        json.writeObject(airport);
        json.close();

        assertThat(out.toString(), equalTo("{\"iata\":\"AAA\",\"latitude\":10.0,\"longitude\":20.0}"));
    }

    @Test
    public void testDeserialization() throws IOException {
        JsonFactory jsonFactory = new MappingJsonFactory();

        Airport airport1 = new Airport.Builder()
                .withIata("aaa")
                .withLatitude(10)
                .withLongitude(20)
                .build();

        Airport airport2 = jsonFactory
                .createParser("{\"iata\":\"AAA\",\"latitude\":10.0,\"longitude\":20.0}")
                .readValueAs(Airport.class);

        assertThat(airport1, equalTo(airport2));
    }
}
