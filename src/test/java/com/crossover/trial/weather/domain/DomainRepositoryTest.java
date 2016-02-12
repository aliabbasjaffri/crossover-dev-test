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

import javax.persistence.EntityManager;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.crossover.trial.weather.domain.IATA.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context-test.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class DomainRepositoryTest {

    @Autowired AirportRepository airportRepository;
    @Autowired DataPointRepository dataPointRepository;

    @Autowired EntityManager manager;

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testAirportRepositorySimple() {
        Airport bos = airportRepository.findOne(valueOf("BOS"));
        Airport ewr = airportRepository.findOne(valueOf("EWR"));
        Airport jfk = airportRepository.findOne(valueOf("JFK"));
        Airport lga = airportRepository.findOne(valueOf("LGA"));
        Airport mmu = airportRepository.findOne(valueOf("MMU"));

        assertThat(bos, notNullValue());
        assertThat(bos.getIata(), equalTo(valueOf("BOS")));
        assertThat(bos.getLatitude(), equalTo(42.364347D));
        assertThat(bos.getLongitude(), equalTo(-71.005181D));

        assertThat(ewr, notNullValue());
        assertThat(ewr.getIata(), equalTo(valueOf("EWR")));
        assertThat(ewr.getLatitude(), equalTo(40.6925D));
        assertThat(ewr.getLongitude(), equalTo(-74.168667D));

        assertThat(jfk, notNullValue());
        assertThat(jfk.getIata(), equalTo(valueOf("JFK")));
        assertThat(jfk.getLatitude(), equalTo(40.639751D));
        assertThat(jfk.getLongitude(), equalTo(-73.778925D));

        assertThat(lga, notNullValue());
        assertThat(lga.getIata(), equalTo(valueOf("LGA")));
        assertThat(lga.getLatitude(), equalTo(40.777245D));
        assertThat(lga.getLongitude(), equalTo(-73.872608D));

        assertThat(mmu, notNullValue());
        assertThat(mmu.getIata(), equalTo(valueOf("MMU")));
        assertThat(mmu.getLatitude(), equalTo(40.79935D));
        assertThat(mmu.getLongitude(), equalTo(-74.4148747D));
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testFindInRangeSmall() {
        Airport ewr = airportRepository.findOne(valueOf("EWR"));
        Airport lga = airportRepository.findOne(valueOf("LGA"));

        Iterator<Airport> airports = airportRepository
                .findInRadius(valueOf("JFK"), 50)
                .iterator();

        assertThat(ewr, equalTo(airports.next()));
        assertThat(lga, equalTo(airports.next()));
    }


    @Test
    @DatabaseSetup("classpath:dbtest/airport_big.xml")
    public void testFindInRangeBig() {
        long count = StreamSupport.stream(airportRepository
                .findInRadius(valueOf("BOS"), 500)
                .spliterator(), false)
        .count();

        assertThat(count, equalTo(429L));
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testFindAllIataCodes() {

        List<IATA> iataCodes = StreamSupport
                .stream(airportRepository.findAllIATACodes().spliterator(), false)
                .collect(Collectors.toList());

        assertThat(iataCodes, equalTo(asList("BOS", "EWR", "JFK", "LGA", "MMU")
                .stream().map(IATA::valueOf)
                .collect(Collectors.toList())));
    }

    @Test
    public void testDataPointRepositorySimple() {
        DataPoint dataPoint = new DataPoint.Builder()
                    .withType(DataPointType.CLOUD_COVER)
                    .withCount(10).withFirst(1)
                    .withSecond(2).withThird(3)
                    .withMean(0)
                    .build();

        dataPointRepository.save(dataPoint);

        DataPoint persisted = dataPointRepository.findOne(dataPoint.getId());

        assertThat(dataPoint, equalTo(persisted));

        dataPointRepository.delete(persisted);

        assertThat(dataPointRepository.count(), equalTo(0L));

    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testDataPointRepositoryMeasurement() {
        Measurement measurement = doTestDataPointRepositoryMeasurement();

        assertThat(measurement, notNullValue());
        assertThat(manager.createQuery("select count(m) from Measurement m")
                .getSingleResult(), equalTo(1L));

        remove(measurement);

        assertThat(manager.createQuery("select count(m) from Measurement m")
                .getSingleResult(), equalTo(0L));
        assertThat(manager.createQuery("select count(p) from DataPoint p")
                .getSingleResult(), equalTo(0L));
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testDataPointRepositoryMeasurementFindLatest() {
        Measurement m1 = doTestDataPointRepositoryMeasurement();
        Measurement m2 = doTestDataPointRepositoryMeasurement();
        Measurement m3 = doTestDataPointRepositoryMeasurement();
        Measurement latest = dataPointRepository
                .findLatestMeasurementForIata(valueOf("BOS"));

        assertThat(m3, equalTo(latest));

        remove(m1);
        remove(m2);
        remove(m3);
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testDataPointRepositoryMeasurementCountTimeWindow() {
        Measurement m1 = doTestDataPointRepositoryMeasurement();
        Measurement m2 = doTestDataPointRepositoryMeasurement();
        Measurement m3 = doTestDataPointRepositoryMeasurement();

        long count = dataPointRepository
                .countTotalMeasurementsInLastTimeWindow(Measurement
                        .timestamp().minusMinutes(1));

        assertThat(count, equalTo(3L));

        remove(m1);
        remove(m2);
        remove(m3);
    }

    @Transactional
    protected void remove(Measurement measurement) {
        measurement = manager.find(Measurement.class, measurement.getId());
        manager.remove(measurement);
    }

    @Transactional
    protected Measurement doTestDataPointRepositoryMeasurement() {
        DataPoint dataPoint = new DataPoint.Builder()
                .withType(DataPointType.CLOUD_COVER)
                .withCount(10).withFirst(1)
                .withSecond(2).withThird(3)
                .withMean(0)
                .build();

        return dataPointRepository.createMeasurement(valueOf("BOS"), dataPoint);
    }

    public static Object getId(Measurement measurement) {
        return measurement.getId();
    }

}
