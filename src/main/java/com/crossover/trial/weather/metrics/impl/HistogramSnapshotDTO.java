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
package com.crossover.trial.weather.metrics.impl;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Snapshot;

public class HistogramSnapshotDTO {

    private final long count;
    private final Snapshot snapshot;

    HistogramSnapshotDTO(Histogram histogram) {
        this.count = histogram.getCount();
        this.snapshot = histogram.getSnapshot();
    }

    public long getCount() {
        return count;
    }

    public double getMedian() {
        return snapshot.getMedian();
    }

    public double get75thPercentile() {
        return snapshot.get75thPercentile();
    }

    public double get95thPercentile() {
        return snapshot.get95thPercentile();
    }

    public double get98thPercentile() {
        return snapshot.get98thPercentile();
    }

    public double get99thPercentile() {
        return snapshot.get99thPercentile();
    }

    public double get999thPercentile() {
        return snapshot.get999thPercentile();
    }

    public long getMax() {
        return snapshot.getMax();
    }

    public double getMean() {
        return snapshot.getMean();
    }

    public long getMin() {
        return snapshot.getMin();
    }

    public double getStdDev() {
        return snapshot.getStdDev();
    }
}
