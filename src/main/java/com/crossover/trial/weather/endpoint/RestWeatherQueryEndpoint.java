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
package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.domain.*;
import com.crossover.trial.weather.metrics.MetricsReport;
import com.crossover.trial.weather.metrics.MetricsService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.eclipse.core.runtime.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RestWeatherQueryEndpoint {

    @Autowired AirportRepository airportRepository;
    @Autowired DataPointRepository dataPointRepository;

    @Autowired MetricsService<?, ?> metricsService;

    @RequestMapping(path="/query/weather/{iata}/{radius}", method=GET,
        produces="application/json")
    @ApiResponses({
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request")
    })
    public List<Data> findInRange(@PathVariable("iata") IATA iata,
                                  @PathVariable("radius") double radius) {

        Assert.isTrue(iata != null);

        metricsService.markRequest(iata, radius);

        return StreamSupport.stream(airportRepository
                .findInRadius(iata, radius).spliterator(), false)
                .map(dataPointRepository::findLatestMeasurementForIata)
                .filter(measurement -> measurement != null)
                .map(Data::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/query/ping", method=GET,
            produces="application/json")
    @ApiResponses(@ApiResponse(code=200, message="OK"))
    public MetricsReport<?, ?> ping() {
        return metricsService.buildReport();
    }

    @JsonAutoDetect(fieldVisibility=ANY)
    static class Data {

        private final Airport airport;
        private final DataPoint data;

        @ApiModelProperty(dataType="java.util.Date")
        private final String timestamp;

        Data(Measurement measurement) {
            airport = measurement.getAirport();
            data = measurement.getPoint();
            timestamp = measurement.getTimestamp()
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }
}
