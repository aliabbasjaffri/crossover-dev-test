package com.crossover.trial.weather;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Profile("production")
public class Documentation {

    @Bean Docket weatherApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("weather")
            .apiInfo(apiInfo("Conditions and Reporting"))
            .useDefaultResponseMessages(false)
            .select().paths(regex("/(collect|query)/.*"))
        .build();
    }


    private ApiInfo apiInfo(String prefix) {
        return new ApiInfoBuilder()
            .title("Weather " + prefix + " API")
            .description("Crossover Development Test Assignment 2 - part 3")
            .contact("Thiago Souza")
            .license("The MIT License")
            .licenseUrl("https://opensource.org/licenses/MIT")
            .version("1.0.0")
        .build();
    }

}
