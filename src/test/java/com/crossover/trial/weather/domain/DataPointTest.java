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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class DataPointTest {

    @Test
    public void testSimple() {
        DataPoint dataPoint = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withFirst(1)
                .withSecond(2)
                .withThird(3)
                .withMean(4)
                .build();

        assertThat(dataPoint.getType(), equalTo(DataPointType.CLOUD_COVER));
        assertThat(dataPoint.getCount(), equalTo(10));
        assertThat(dataPoint.getFirst(), equalTo(1));
        assertThat(dataPoint.getSecond(), equalTo(2));
        assertThat(dataPoint.getThird(), equalTo(3));
        assertThat(dataPoint.getMean(), equalTo(4D));

    }

    @Test
    public void testEquality() {
        DataPoint dataPoint1 = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withFirst(1)
                .withSecond(2)
                .withThird(3)
                .withMean(4)
                .build();

        DataPoint dataPoint2 = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withFirst(1)
                .withSecond(2)
                .withThird(3)
                .withMean(4)
                .build();

        assertThat(dataPoint1, equalTo(dataPoint2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_1() {
        new DataPoint.Builder()
                .withCount(10)
                .withFirst(1)
                .withSecond(2)
                .withThird(3)
                .withMean(4)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_2() {
        new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withFirst(1)
                .withSecond(2)
                .withThird(3)
                .withMean(4)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_3() {
        new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withSecond(2)
                .withThird(3)
                .withMean(4)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_4() {
        new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withFirst(1)
                .withThird(3)
                .withMean(4)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_5() {
        new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withFirst(1)
                .withSecond(2)
                .withMean(4)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_6() {
        new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10)
                .withFirst(1)
                .withSecond(2)
                .withThird(3)
                .build();
    }

    @Test
    public void testDataTypeRangeValidityRules() {
        Airport airport = new Airport.Builder()
                .withIata("AAA")
                .withLatitude(10)
                .withLongitude(20)
                .build();

        DataPoint validCloudCover = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();
        DataPoint invalidCloudCover = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(-1000)
                .build();

        DataPoint validHumidity = new DataPoint.Builder()
                .withType(DataPointType.HUMIDITY)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();
        DataPoint invalidHumidity = new DataPoint.Builder()
                .withType(DataPointType.HUMIDITY)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(-1000)
                .build();

        DataPoint validPrecipitation = new DataPoint.Builder()
                .withType(DataPointType.PRECIPITATION)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();
        DataPoint invalidPrecipitation = new DataPoint.Builder()
                .withType(DataPointType.PRECIPITATION)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(-1000)
                .build();

        DataPoint validPressure = new DataPoint.Builder()
                .withType(DataPointType.PRESSURE)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(700)
                .build();
        DataPoint invalidPressure = new DataPoint.Builder()
                .withType(DataPointType.PRESSURE)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();

        DataPoint validTemperature = new DataPoint.Builder()
                .withType(DataPointType.TEMPERATURE)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();
        DataPoint invalidTemperature = new DataPoint.Builder()
                .withType(DataPointType.TEMPERATURE)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(-1000)
                .build();

        DataPoint validWind = new DataPoint.Builder()
                .withType(DataPointType.WIND)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(1)
                .build();
        DataPoint invalidWind = new DataPoint.Builder()
                .withType(DataPointType.WIND)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(-1)
                .build();

        assertThat(validCloudCover.createMeasurement(airport), notNullValue());
        assertThat(invalidCloudCover.createMeasurement(airport), nullValue());

        assertThat(validHumidity.createMeasurement(airport), notNullValue());
        assertThat(invalidHumidity.createMeasurement(airport), nullValue());

        assertThat(validPrecipitation.createMeasurement(airport), notNullValue());
        assertThat(invalidPrecipitation.createMeasurement(airport), nullValue());

        assertThat(validPressure.createMeasurement(airport), notNullValue());
        assertThat(invalidPressure.createMeasurement(airport), nullValue());

        assertThat(validTemperature.createMeasurement(airport), notNullValue());
        assertThat(invalidTemperature.createMeasurement(airport), nullValue());

        assertThat(validWind.createMeasurement(airport), notNullValue());
        assertThat(invalidWind.createMeasurement(airport), nullValue());
    }
}
