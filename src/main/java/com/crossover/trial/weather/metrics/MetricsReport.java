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
package com.crossover.trial.weather.metrics;

import com.crossover.trial.weather.domain.Airport;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.SortedSet;
import java.util.TreeSet;

public class MetricsReport<A, R> {

    @JsonProperty("datasize")
    private final long dataSize;

    @JsonProperty("airport")
    private final SortedSet<AirportMetric<A>> airportMetrics;

    @JsonProperty("radius")
    @ApiModelProperty(dataType="com.crossover.trial.weather.metrics.impl.HistogramSnapshotDTO")
    private final R radiusMetrics;

    private MetricsReport(long dataSize, SortedSet<AirportMetric<A>> airportMetrics, R radiusFrequency) {
        this.dataSize = dataSize;
        this.airportMetrics = airportMetrics;
        this.radiusMetrics = radiusFrequency;
    }

    public long getDataSize() {
        return dataSize;
    }

    public SortedSet<AirportMetric<A>> getAirportMetrics() {
        return airportMetrics;
    }

    public R getRadiusMetrics() {
        return radiusMetrics;
    }

    public static class Builder<A, R> {

        private long dataSize;
        private final SortedSet<AirportMetric<A>> airportFrequency =
                new TreeSet<>();
        private R radiusFrequency;

        public Builder<A, R> withDataSize(long dataSize) {
            this.dataSize = dataSize;
            return this;
        }

        public Builder<A, R> withAirportMetric(Airport airport, A metric) {
            airportFrequency.add(new AirportMetric<>(airport, metric));
            return this;
        }

        public Builder<A, R> withRadiusFrequency(R radiusFrequency) {
            this.radiusFrequency = radiusFrequency;
            return this;
        }

        public MetricsReport<A, R> build() {
            return new MetricsReport<>(dataSize, airportFrequency, radiusFrequency);
        }

    }
}
