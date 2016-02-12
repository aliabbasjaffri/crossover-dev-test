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

import com.crossover.trial.weather.domain.DomainRepositoryTest;
import com.crossover.trial.weather.domain.Measurement;
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

import static com.crossover.trial.weather.domain.IATA.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:context-test.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class RestWeatherCollectorEndpointTest {

    private MockMvc mockMvc;

    @Autowired RestWeatherCollectorEndpoint endpoint;
    @Autowired EntityManager manager;

    @Before
    public void setup() {
        FormattingConversionService converter = new FormattingConversionService();
        converter.addConverter(new DataPointTypeConverter());
        converter.addConverter(new IATASpringConverter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(endpoint)
                .setConversionService(converter)
                .build();
    }

    @Test
    public void testPing() throws Exception {
        mockMvc.perform(get("/collect/ping"))
                .andExpect(status().isOk());
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testUpdateWeatherValid() throws Exception {
        mockMvc.perform(post("/collect/weather/bos/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                    "\"mean\": 0.0," +
                    "\"first\": 10," +
                    "\"second\": 20," +
                    "\"third\": 30," +
                    "\"count\": 40" +
                "}"))
                .andExpect(status().isOk());

        Measurement measurement = manager
                .createQuery("select m from Measurement m", Measurement.class)
                .getSingleResult();

        assertThat(measurement, notNullValue());
        assertThat(measurement.getAirport().getIata(),  equalTo(valueOf("BOS")));
        assertThat(measurement.getPoint().getMean(),    equalTo(0D));
        assertThat(measurement.getPoint().getFirst(),   equalTo(10));
        assertThat(measurement.getPoint().getSecond(),  equalTo(20));
        assertThat(measurement.getPoint().getThird(),   equalTo(30));
        assertThat(measurement.getPoint().getCount(),   equalTo(40));

        remove(measurement);
    }

    @Transactional
    protected void remove(Measurement measurement) {
        measurement = manager.find(Measurement.class, DomainRepositoryTest.getId(measurement));
        manager.remove(measurement);
    }

    @Test
    public void testUpdateWeatherInvalid_1() throws Exception {
        mockMvc.perform(post("/collect/weather/abc/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"mean\": 0.0," +
                        "\"first\": 10," +
                        "\"second\": 20," +
                        "\"third\": 30," +
                        "\"count\": 40" +
                        "}"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testUpdateWeatherInvalid_2() throws Exception {
        mockMvc.perform(post("/collect/weather/bos/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"mean\": -1000.0," +
                        "\"first\": 10," +
                        "\"second\": 20," +
                        "\"third\": 30," +
                        "\"count\": 40" +
                        "}"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testUpdateWeatherInvalid_3() throws Exception {
        mockMvc.perform(post("/collect/weather/bos/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"first\": 10," +
                        "}"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testListAirportIataCodes() throws Exception {
        mockMvc.perform(get("/collect/airports"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("[ { \"iata\": \"BOS\" }," +
                                "{ \"iata\": \"EWR\" }," +
                                "{ \"iata\": \"JFK\" }," +
                                "{ \"iata\": \"LGA\" }," +
                                "{ \"iata\": \"MMU\" }]", false));
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testGetAirportValid() throws Exception {
        mockMvc.perform(get("/collect/airport/bos"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{\"iata\":\"BOS\", " +
                                "\"latitude\": 42.364347, " +
                                "\"longitude\": -71.005181}"));
    }

    @Test
    public void testRegisterAirport() throws Exception {
        mockMvc.perform(post("/collect/airport/bos/42.364347/-71.005181"))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .json("{\"iata\":\"BOS\", " +
                                "\"latitude\": 42.364347, " +
                                "\"longitude\": -71.005181}"));
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testDeleteAirport() throws Exception {
        mockMvc.perform(delete("/collect/airport/bos"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/collect/airport/bos"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testDeleteAirportWithMeasurements() throws Exception {
        mockMvc.perform(post("/collect/weather/bos/cloud_cover")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{ " +
                        "\"mean\": 0.0," +
                        "\"first\": 10," +
                        "\"second\": 20," +
                        "\"third\": 30," +
                        "\"count\": 40" +
                        "}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/collect/airport/bos"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/collect/airport/bos"))
                .andExpect(status().isNotFound());
    }


    @Test
    @DatabaseSetup("classpath:dbtest/airport_small.xml")
    public void testGetAirportInvalid() throws Exception {
        mockMvc.perform(get("/collect/airport/abc"))
                .andExpect(status().is4xxClientError());
    }
}
