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

import com.crossover.trial.weather.metrics.MetricsService;
import com.crossover.trial.weather.util.DataPointTypeConverter;
import com.crossover.trial.weather.util.iata.IATASpringConverter;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context-test.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class RestWeatherQueryEndpointTest {

    private MockMvc mockMvc;

    @Autowired RestWeatherQueryEndpoint queryEndpoint;
    @Autowired RestWeatherCollectorEndpoint collectorEndpoint;

    @Autowired MetricsService<?, ?> metricsService;
    @Autowired EntityManager manager;

    @Before
    public void setup() {
        FormattingConversionService converter = new FormattingConversionService();
        converter.addConverter(new DataPointTypeConverter());
        converter.addConverter(new IATASpringConverter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(queryEndpoint, collectorEndpoint)
                .setConversionService(converter)
                .build();
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testFindInRange() throws Exception {

        populateData();

        mockMvc.perform(get("/query/weather/jfk/50"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                    "\"airport\":{\"iata\":\"EWR\",\"latitude\":40.6925,\"longitude\":-74.168667}," +
                    "\"data\":{\"mean\":20.0,\"first\":10,\"second\":20,\"third\":30,\"count\":40,\"type\":\"CLOUD_COVER\"}" +
                    "}]", false));

        removeAllData();
        metricsService.clear();
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testPing() throws Exception {
        populateData();

        mockMvc.perform(get("/query/ping"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"datasize\":3," +
                        "\"airport\":[]," +
                        "\"radius\":{" +
                            "\"count\":0," +
                            "\"min\":0," +
                            "\"max\":0," +
                            "\"mean\":0.0," +
                            "\"stdDev\":0.0," +
                            "\"98thPercentile\":0.0," +
                            "\"99thPercentile\":0.0," +
                            "\"999thPercentile\":0.0," +
                            "\"75thPercentile\":0.0," +
                            "\"95thPercentile\":0.0," +
                            "\"median\":0.0}}"));

        mockMvc.perform(get("/query/weather/jfk/50"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/query/ping"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"datasize\":3," +
                        "\"airport\":[{" +
                            "\"airport\":{\"iata\":\"JFK\",\"latitude\":40.639751,\"longitude\":-73.778925}," +
                            "\"data\":{\"count\":1}" +
                        "}]," +
                        "\"radius\":{\"count\":1,\"min\":5,\"max\":5,\"mean\":5.0,\"75thPercentile\":5.0," +
                        "\"median\":5.0,\"98thPercentile\":5.0,\"95thPercentile\":5.0,\"99thPercentile\":5.0," +
                        "\"999thPercentile\":5.0,\"stdDev\":0.0}}", false));

        removeAllData();
        metricsService.clear();
    }

    private void populateData() throws Exception {
        mockMvc.perform(post("/collect/weather/ewr/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"mean\": 0.0," +
                        "\"first\": 10," +
                        "\"second\": 20," +
                        "\"third\": 30," +
                        "\"count\": 40" +
                        "}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/collect/weather/ewr/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"mean\": 10.0," +
                        "\"first\": 10," +
                        "\"second\": 20," +
                        "\"third\": 30," +
                        "\"count\": 40" +
                        "}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/collect/weather/ewr/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"mean\": 20.0," +
                        "\"first\": 10," +
                        "\"second\": 20," +
                        "\"third\": 30," +
                        "\"count\": 40" +
                        "}"))
                .andExpect(status().isOk());
    }

    @Transactional
    protected void removeAllData() {
        manager.createQuery("delete from Measurement").executeUpdate();
    }

}
