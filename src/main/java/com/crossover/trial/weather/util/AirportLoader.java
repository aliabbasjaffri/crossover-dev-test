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
package com.crossover.trial.weather.util;

import com.crossover.trial.weather.domain.Airport;
import com.crossover.trial.weather.domain.AirportRepository;
import com.crossover.trial.weather.domain.IATA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.hasText;

@Service
public class AirportLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AirportLoader.class);

    @Autowired AirportRepository airportRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("airports") && !args.getOptionValues("airports").isEmpty()) {
            File dataFile = new File(args.getOptionValues("airports").get(0));
            if (dataFile.canRead() && dataFile.isFile() && dataFile.exists()) {
                log.info("Loading airports from [{}]", dataFile);

                try (Stream<String> lines = Files.lines(dataFile.toPath())) {
                    lines.map(Row::new)
                         .filter(Row::expectedTotalColumns)
                         .filter(Row::expectedColumnsFilled)
                         .map(Row::parse)
                         .filter(airport -> airport != null)
                         .forEach(airportRepository::save);
                }

            } else
                log.error("Specified airports data file is not readable [{}]", dataFile.toPath());

        }
    }

    static class Row {

        static final int IATA_COLUMN = 4;
        static final int LATITUDE_COLUMN = 6;
        static final int LONGITUDE_COLUMN = 7;

        final String source;
        final String[] parsed;

        Row(String source) {
            this.source = source;
            parsed = source.split(",");
        }

        boolean expectedTotalColumns() {
            return parsed.length >= 8;
        }

        boolean expectedColumnsFilled() {
            return hasText(parsed[IATA_COLUMN]) &&
                   hasText(parsed[LATITUDE_COLUMN]) &&
                   hasText(parsed[LONGITUDE_COLUMN]);
        }

        Airport parse() {
            try {
                String strIata = parsed[IATA_COLUMN];
                if (strIata.startsWith("\""))
                    strIata = strIata.replaceFirst("\"(.*)\"", "$1");

                Airport airport = new Airport.Builder()
                    .withIata(IATA.valueOf(strIata))
                    .withLatitude(Double.valueOf(parsed[LATITUDE_COLUMN]))
                    .withLongitude(Double.valueOf(parsed[LONGITUDE_COLUMN]))
                .build();
                log.info("\n\t(result): AIRPORT REGISTERED\n\t(output): {}\n\t(source): {}", airport, source);
                return airport;
            } catch (Exception e) {
                log.error("\n\t(result): INVALID INPUT\n\t(output): {}\n\t(source): {}", e.getMessage(), source);
                return null;
            }
        }
    }
}
