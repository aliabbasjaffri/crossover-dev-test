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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class AirportRepositoryImpl implements AirportRepositoryCustom {

    @Autowired AirportRepository airportRepository;
    @Autowired EntityManager manager;

    @Override
    @Transactional(readOnly=true)
    public Iterable<Airport> findInRadius(IATA fromIata, double distanceInKm) {
        Assert.isTrue(fromIata != null, "fromIata is required");
        Assert.isTrue(distanceInKm > 0, "distanceInKm must be positive");

        Airport fromAirport = airportRepository.findOne(fromIata);
        if (fromAirport == null)
            return Collections.emptyList();

        return manager.createQuery("select a from Airport a where iata != :fromIata", Airport.class)
                .setParameter("fromIata", fromIata)
                .getResultList().parallelStream()
                .filter(toAirport -> fromAirport.calculateDistance(toAirport) <= distanceInKm)
                .collect(Collectors.toList());
    }

}
