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

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Measurement Entity
 *
 * Represents the measurement of a {@link DataPoint} for a specific {@link Airport}
 * and a specified date and time
 */
@Entity @Table(name="MEASUREMENT")
public class Measurement {

    @EmbeddedId
    private PK id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "iata", referencedColumnName="iata", insertable=false, updatable=false)
    private Airport airport;

    @OneToOne(optional=false, cascade=CascadeType.REMOVE)
    @JoinColumn(name = "idPoint", referencedColumnName="id", insertable=false, updatable=false)
    private DataPoint point;

    @Column(insertable=false, updatable=false)
    private ZonedDateTime timestamp;

    protected Measurement() {}

    Measurement(Airport airport, DataPoint point) {
        this.airport = airport;
        this.point = point;

        timestamp = timestamp();

        id = new PK(airport, point, timestamp);
    }

    PK getId() {
        return id;
    }

    public Airport getAirport() {
        return airport;
    }

    public DataPoint getPoint() {
        return point;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public static ZonedDateTime timestamp() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Embeddable
    static class PK implements Serializable {

        private String iata;
        private Long idPoint;

        private ZonedDateTime timestamp;

        protected PK() {}

        private PK(Airport airport, DataPoint dataPoint, ZonedDateTime timestamp) {
            this.iata = airport.getIata().getCode();
            this.idPoint = dataPoint.getId();
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PK pk = (PK) o;

            if (iata != null ? !iata.equals(pk.iata) : pk.iata != null) return false;
            if (idPoint != null ? !idPoint.equals(pk.idPoint) : pk.idPoint != null) return false;
            return timestamp != null ? timestamp.equals(pk.timestamp) : pk.timestamp == null;

        }

        @Override
        public int hashCode() {
            int result = iata != null ? iata.hashCode() : 0;
            result = 31 * result + (idPoint != null ? idPoint.hashCode() : 0);
            result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
            return result;
        }
    }
}
