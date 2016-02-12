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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class RestWeatherCollectorEndpoint {

    private static final Logger log = LoggerFactory.getLogger(RestWeatherCollectorEndpoint.class);

    @Autowired AirportRepository airportRepository;
    @Autowired DataPointRepository dataPointRepository;

    @RequestMapping(path="/collect/weather/{iata}/{pointType}", method=POST,
        consumes="application/json")
    @ApiResponses({
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=400, message="Bad Request")
    })
    public ResponseEntity<Void> updateWeather(@PathVariable("iata") IATA iata,
                                        @PathVariable("pointType") DataPointType pointType,
                                        @RequestBody Data data) {

        Assert.isTrue(iata != null);
        Assert.isTrue(pointType != null);
        Assert.isTrue(data != null);

        Airport airport = airportRepository.findOne(iata);
        if (airport == null)
            return badRequest()
                    .build();

        DataPoint dataPoint = createDataPoint(data, pointType);
        if (dataPoint == null)
            return badRequest()
                    .build();

        Measurement measurement = updateWeather(airport, dataPoint);

        return measurement == null ?
            badRequest().build() :
            ok().build();
    }

    private DataPoint createDataPoint(Data pointDTO, DataPointType pointType) {
        try {
            return new DataPoint.Builder()
                    .withMean(pointDTO.mean)
                    .withFirst(pointDTO.first)
                    .withSecond(pointDTO.second)
                    .withThird(pointDTO.third)
                    .withCount(pointDTO.count)
                    .withType(pointType)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    protected Measurement updateWeather(Airport airport, DataPoint dataPoint) {
        return dataPointRepository.createMeasurement(airport, dataPoint);
    }

    @Transactional(readOnly=true)
    @RequestMapping(path="/collect/airports", method=GET,
        produces="application/json")
    @ApiResponses(@ApiResponse(code=200, message="OK"))
    public List<Airport> listAirportIataCodes() {
        return StreamSupport.stream(airportRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    @RequestMapping(path="/collect/airport/{iata}", method=GET,
            produces="application/json")
    @ApiResponses({
            @ApiResponse(code=200, message="OK"),
            @ApiResponse(code=404, message="Not Found"),
            @ApiResponse(code=400, message="Bad Request")
    })
    public ResponseEntity<Airport> getAirport(@PathVariable("iata") IATA iata) {
        Assert.isTrue(iata != null);

        Airport airport = airportRepository.findOne(iata);
        return airport == null ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(airport, HttpStatus.OK);
    }

    // {long:.+} - So Spring won't threat dot as file extension
    @RequestMapping(path="/collect/airport/{iata}/{lat}/{long:.+}", method=POST,
            produces="application/json")
    @ApiResponses({
            @ApiResponse(code=201, message="Created"),
            @ApiResponse(code=400, message="Bad Request")
    })
    public ResponseEntity<Airport> registerAirport(
            @PathVariable("iata") IATA iata,
            @PathVariable("lat") double latitude,
            @PathVariable("long") double longitude) {
        Assert.isTrue(iata != null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(airportRepository.save(new Airport.Builder()
                    .withIata(iata)
                    .withLatitude(latitude)
                    .withLongitude(longitude)
                        .build()));
    }

    @Transactional
    @RequestMapping(path="/collect/airport/{iata}", method=DELETE)
    @ApiResponses({
            @ApiResponse(code=204, message="No Content"),
            @ApiResponse(code=404, message="Not Found"),
            @ApiResponse(code=400, message="Bad Request")
    })
    public ResponseEntity<Void> deletAirport(@PathVariable("iata") IATA iata) {

        if (!airportRepository.exists(iata))
            return ResponseEntity.notFound()
                    .build();

        dataPointRepository.deleteAllMeasurements(iata);
        airportRepository.delete(iata);

        return ResponseEntity.noContent()
                .build();
    }

    @RequestMapping(path="/collect/ping", method=GET)
    public void ping() {}

    @JsonAutoDetect(fieldVisibility=ANY)
    private static class Data {

        @ApiModelProperty(required=true)
        private Double mean;

        @ApiModelProperty(required=true)
        private Integer first;

        @ApiModelProperty(required=true)
        private Integer second;

        @ApiModelProperty(required=true)
        private Integer third;

        @ApiModelProperty(required=true)
        private Integer count;
    }
}
