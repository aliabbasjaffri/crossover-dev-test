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


import com.codahale.metrics.Meter;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.domain.DataPointRepository;
import com.crossover.trial.weather.domain.DataPointType;
import com.crossover.trial.weather.domain.IATA;
import com.crossover.trial.weather.metrics.impl.HistogramSnapshotDTO;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context-test.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class MetricsServiceTest {

    @Autowired DataPointRepository dataPointRepository;
    @Autowired MetricsService<Meter, HistogramSnapshotDTO> metricsService;

    @Test @Transactional
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testSimple() {
        DataPoint dataPoint = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();

        dataPointRepository.createMeasurement(IATA.valueOf("BOS"), dataPoint);

        MetricsReport<Meter, HistogramSnapshotDTO> report;

        metricsService.markRequest(IATA.valueOf("BOS"), 50);
        report = metricsService.buildReport();

        assertThat(report.getDataSize(), equalTo(1L));
        assertThat(report.getAirportMetrics().size(), equalTo(1));
        assertThat(report.getRadiusMetrics().getMax(), equalTo(5L));

        metricsService.markRequest(IATA.valueOf("JFK"), 100);
        report = metricsService.buildReport();

        assertThat(report.getAirportMetrics().size(), equalTo(2));
        assertThat(report.getRadiusMetrics().getMax(), equalTo(10L));

        metricsService.markRequest(IATA.valueOf("ZZZ"), 200);
        report = metricsService.buildReport();

        assertThat(report.getAirportMetrics().size(), equalTo(2));
        assertThat(report.getRadiusMetrics().getMax(), equalTo(20L));

        metricsService.clear();
    }

}
