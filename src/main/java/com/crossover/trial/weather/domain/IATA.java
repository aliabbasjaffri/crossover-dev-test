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

import com.crossover.trial.weather.util.iata.IATADeserializer;
import com.crossover.trial.weather.util.iata.IATASerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * International Air Transport Association (IATA) Code
 *
 * It's a standard 3-letter code used to identify {@link Airport}
 *
 */
@Embeddable
@JsonSerialize(using=IATASerializer.class)
@JsonDeserialize(using=IATADeserializer.class)
public final class IATA implements Comparable<IATA>, Serializable {

    @Column(name="iata")
    private String code;

    public static IATA valueOf(String code) {
        Assert.hasText(code, "iata code is required");
        Assert.isTrue(code.matches("[a-zA-Z]{3}"), "iata must be a 3-letter code");
        return new IATA(code);
    }

    protected IATA() {}

    private IATA(String code) {
        this.code = code.toUpperCase();
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IATA iata = (IATA) o;

        return code.equals(iata.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public int compareTo(IATA o) {
        return code.compareTo(o.code);
    }
}
