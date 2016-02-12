# Crossover Development Test Assignment 2 - part 3

This is a weather conditions and reporting web application.

## Building and Testing

You'll need a Java 8 compatible JDK and Maven 3 installed.

To build:
```
$ cd weather-dist_part3
$ mvn clean package -DskipTests=true
```

To test:
```
$ cd weather-dist_part3
$ mvn clean test
```

## Running

After building, the final executable jar will be available as target/dist-1.0.0.jar

To run:
```
$ cd weather-dist_part3
$ java -jar target/dist-1.0.0.jar
```

Upon server initialization, API will be available at http://localhost:8080/

The server can also start and load airport information from an external data file.
 
To run and load:
```
$ cd weather-dist_part3
$ java -jar target/dist-1.0.0.jar --airports=<path_to_airports_csv>
```
Upon server initialization, API will be available at http://localhost:8080/ along with loaded airport data

## Management

The application is packed with Spring Boot Actuator and thus there are several rest endpoints for
diagnosing. Go to [it's documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) to learn more.

## REST API Documentation (WIP)

The REST API is self-documented through Swagger2. Go [here](http://localhost:8080/swagger-ui.html) to access the online documentation.

Note: This documentation is considered a work in progress and still needs some tweaking

## Remarks

The server boots with an embedded in-memory H2 database. So, restarting the server
means resetting it's data.

As the application is built on JPA, little effort is needed 
to switch to a fully-fledged remote RDBMS (that is supported by Hibernate).

The application is also built on Spring Data, this means that switching
to a NoSQL database is also straight forward.

## Acknowledgements

Thanks Crossover for this opportunity! It was quite fun!

## LICENSE

Code and documentation released under [The MIT License (MIT)](LICENSE).