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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import static com.crossover.trial.weather.util.Constants.EARTH_RADIUS;

/**
 *  Airport Entity
 *
 *  Represents an Airport identified by IATA along with it's latitude and longitude
 */
@Entity @Table(name="AIRPORT")
@JsonDeserialize(builder = Airport.Builder.class)
public class Airport implements Comparable<Airport> {

    @EmbeddedId
    @ApiModelProperty(dataType="java.lang.String")
    private IATA iata;

    @Column(length=2, precision=5)
    private double latitude;

    @Column(length=3, precision=5)
    private double longitude;

    // JPA requires this
    protected Airport() {}

    private Airport(IATA iata, double latitude, double longitude) {
        this.iata = iata;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public IATA getIata() {
        return iata;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double calculateDistance(Airport toAirport) {
        Assert.isTrue(toAirport != null, "airport is required");
        double deltaLat = Math.toRadians(toAirport.latitude - latitude);
        double deltaLon = Math.toRadians(toAirport.longitude - longitude);
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(latitude) * Math.cos(toAirport.latitude);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS * c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airport airport = (Airport) o;

        return iata.equals(airport.iata);

    }

    @Override
    public int hashCode() {
        return iata.hashCode();
    }

    @Override
    public String toString() {
        return "Airport{" +
                "iata='" + iata + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int compareTo(Airport o) {
        return iata.compareTo(o.iata);
    }

    @JsonPOJOBuilder
    public static class Builder {

        private IATA iata;
        private Double latitude;
        private Double longitude;

        public Builder withIata(String iata) {
            return withIata(IATA.valueOf(iata));
        }

        public Builder withIata(IATA iata) {
            Assert.isTrue(iata != null, "iata is required");
            this.iata = iata;
            return this;
        }

        public Builder withLatitude(double latitude) {
            Assert.isTrue(latitude >= -90 && latitude <= 90, "latitude must be inside range [-90.0, 90.0]");
            this.latitude = latitude;
            return this;
        }

        public Builder withLongitude(double longitude) {
            Assert.isTrue(longitude >= -180 && longitude < 180, "latitude must be inside range [-180.0, 180.0)");
            this.longitude = longitude;
            return this;
        }

        public Airport build() {
            Assert.isTrue(iata != null, "iata is required");
            Assert.isTrue(latitude != null, "latitude is required");
            Assert.isTrue(longitude != null, "longitude is required");
            return new Airport(iata, latitude, longitude);
        }
    }
}
