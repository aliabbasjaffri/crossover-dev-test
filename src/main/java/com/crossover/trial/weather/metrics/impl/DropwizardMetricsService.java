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
package com.crossover.trial.weather.metrics.impl;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.crossover.trial.weather.domain.Airport;
import com.crossover.trial.weather.domain.AirportRepository;
import com.crossover.trial.weather.domain.DataPointRepository;
import com.crossover.trial.weather.domain.IATA;
import com.crossover.trial.weather.metrics.MetricsReport;
import com.crossover.trial.weather.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.crossover.trial.weather.domain.Measurement.timestamp;

@Service
public class DropwizardMetricsService implements MetricsService<Meter, HistogramSnapshotDTO> {

    @Autowired AirportRepository airportRepository;
    @Autowired DataPointRepository dataPointRepository;

    private MetricRegistry registry = new MetricRegistry();
    private final ReadWriteLock registryLock = new ReentrantReadWriteLock();

    @Override
    public void markRequest(IATA iata, double radius) {
        Assert.isTrue(iata != null, "iata is required");

        Lock read = registryLock.readLock();
        read.lock();
        try {
            registry.meter(iata.getCode()).mark();
            registry.histogram("radius")
                    .update(Double.valueOf(radius).intValue() / 10);
        } finally {
            read.unlock();
        }
    }

    @Override
    public MetricsReport<Meter, HistogramSnapshotDTO> buildReport() {
        Lock read = registryLock.readLock();
        read.lock();
        try {
            MetricsReport.Builder<Meter, HistogramSnapshotDTO> builder = new MetricsReport.Builder<Meter, HistogramSnapshotDTO>()
                    .withRadiusFrequency(new HistogramSnapshotDTO(registry
                            .histogram("radius")))
                    .withDataSize(dataPointRepository
                            .countTotalMeasurementsInLastTimeWindow(timestamp()
                                    .minusDays(1)));

            registry.getMeters().entrySet().stream().forEach(entry -> {
                Airport airport = airportRepository.findOne(IATA.valueOf(entry.getKey()));
                if (airport == null)
                    registry.remove(entry.getKey());
                else {
                    Meter meter = registry.meter(entry.getKey());
                    if (meter != null)
                        builder.withAirportMetric(airport, meter);
                }
            });

            return builder.build();
        } finally {
            read.unlock();
        }
    }

    @Override
    public void clear() {
        Lock write = registryLock.readLock();
        write.lock();
        try {
            registry = new MetricRegistry();
        } finally {
            write.unlock();
        }
    }
}
