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

public enum DataPointType {

    WIND() {
        @Override
        boolean isInsideRange(double mean) {
            return mean > 0;
        }
    },
    TEMPERATURE() {
        @Override
        boolean isInsideRange(double mean) {
            return mean >= -50 && mean < 100;
        }
    },
    HUMIDITY() {
        @Override
        boolean isInsideRange(double mean) {
            return mean >= 0 && mean < 100;
        }
    },
    PRESSURE() {
        @Override
        boolean isInsideRange(double mean) {
            return mean >= 650 && mean < 800;
        }
    },
    CLOUD_COVER() {
        @Override
        boolean isInsideRange(double mean) {
            return mean >= 0 && mean < 100;
        }
    },
    PRECIPITATION() {
        @Override
        boolean isInsideRange(double mean) {
            return mean >= 0 && mean < 100;
        }
    };

    abstract boolean isInsideRange(double mean);
}
