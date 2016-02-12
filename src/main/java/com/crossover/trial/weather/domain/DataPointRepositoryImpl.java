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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;

@Component
public class DataPointRepositoryImpl implements DataPointRepositoryCustom {

    @Autowired EntityManager manager;
    @Autowired AirportRepository airportRepository;

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public Measurement createMeasurement(IATA iata, DataPoint dataPoint) {
        Assert.isTrue(iata != null, "iata is required");
        Assert.isTrue(dataPoint != null, "dataPoint is required");

        Airport airport = airportRepository.findOne(iata);
        if (airport == null)
            return null;

        return createMeasurement(airport, dataPoint);
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public Measurement createMeasurement(Airport airport, DataPoint dataPoint) {
        Assert.isTrue(airport != null, "airport is required");
        Assert.isTrue(dataPoint != null, "dataPoint is required");

        manager.persist(dataPoint);
        Measurement measurement = dataPoint.createMeasurement(airport);
        if (measurement != null)
            manager.persist(measurement);
        else
            manager.remove(dataPoint);

        return measurement;
    }

    @Override
    @Transactional(readOnly=true)
    public Measurement findLatestMeasurementForIata(IATA iata) {
        try {
            Assert.isTrue(iata != null, "iata is required");
            return manager.createQuery("select m from Measurement m " +
                    "where m.airport.iata = :iata " +
                    "order by timestamp desc", Measurement.class)
                    .setParameter("iata", iata)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly=true)
    public Measurement findLatestMeasurementForIata(Airport airport) {
        Assert.isTrue(airport != null, "airport is required");
        return findLatestMeasurementForIata(airport.getIata());
    }

    @Override
    @Transactional
    public void deleteAllMeasurements(IATA iata) {
        Assert.isTrue(iata != null, "iata is required");
        manager.createQuery("delete from Measurement where id.iata = :iata")
                .setParameter("iata", iata.getCode())
                .executeUpdate();
    }
}
