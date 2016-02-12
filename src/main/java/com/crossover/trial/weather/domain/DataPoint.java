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
import org.springframework.util.Assert;

import javax.persistence.*;

/**
 *  DataPoint Entity
 *
 *  Represents a sensor data point of a specific {@link DataPointType}
 */
@Entity @Table(name="DATAPOINT")
@JsonDeserialize(builder=DataPoint.Builder.class)
public class DataPoint {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=100, precision=10)
    private double mean;
    private int first;
    private int second;
    private int third;
    private int count;
    private DataPointType type;

    protected DataPoint() {}

    private DataPoint(double mean, int first, int second, int third, int count, DataPointType type) {
        this.mean = mean;
        this.first = first;
        this.second = second;
        this.third = third;
        this.count = count;
        this.type = type;
    }

    Long getId() {
        return id;
    }

    public double getMean() {
        return mean;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public int getThird() {
        return third;
    }

    public int getCount() {
        return count;
    }

    public DataPointType getType() {
        return type;
    }

    Measurement createMeasurement(Airport airport) {
        Assert.isTrue(airport != null, "airport is required");
        if (type.isInsideRange(mean))
            return new Measurement(airport, this);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPoint dataPoint = (DataPoint) o;

        if (Double.compare(dataPoint.mean, mean) != 0) return false;
        if (first != dataPoint.first) return false;
        if (second != dataPoint.second) return false;
        if (third != dataPoint.third) return false;
        if (count != dataPoint.count) return false;
        if (id != null ? !id.equals(dataPoint.id) : dataPoint.id != null) return false;
        return type == dataPoint.type;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        temp = Double.doubleToLongBits(mean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + first;
        result = 31 * result + second;
        result = 31 * result + third;
        result = 31 * result + count;
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "mean=" + mean +
                ", first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", count=" + count +
                ", type=" + type +
                '}';
    }

    @JsonPOJOBuilder
    public static class Builder {

        private Double mean;
        private Integer first;
        private Integer second;
        private Integer third;
        private Integer count;
        private DataPointType type;

        public Builder withMean(double mean) {
            this.mean = mean;
            return this;
        }

        public Builder withFirst(int first) {
            this.first = first;
            return this;
        }

        public Builder withSecond(int second) {
            this.second = second;
            return this;
        }

        public Builder withThird(int third) {
            this.third = third;
            return this;
        }

        public Builder withCount(int count) {
            this.count = count;
            return this;
        }

        public Builder withType(DataPointType type) {
            this.type = type;
            return this;
        }

        public DataPoint build() {
            Assert.isTrue(mean != null, "mean is required");
            Assert.isTrue(first != null, "first is required");
            Assert.isTrue(second != null, "second is required");
            Assert.isTrue(third != null, "third is required");
            Assert.isTrue(count != null, "count is required");
            Assert.isTrue(type != null, "type is required");
            return new DataPoint(mean, first, second, third, count, type);
        }
    }


}
