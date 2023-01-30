package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
We are now set up with the Currency Exchange microservice. It is talking to an in memory db and we have created a simple REST API.
And now we want to set up the Currency Conversion Microservice.
We'll add in all the dependencies that we chose when we were creating Currency Exchange Service i.e. DevTools, Actuator, Web and Config Client.

First we would configure application.properties. The ports that we allocate for the currency-conversion-microservice are 8100, 8101, 8102 and so on.
Assign an application name - spring.application.name = currency-conversion and server.port = 8100.
On launching this, the currency-conversion-service application is up and running, and it's running on port 8100.
 */
@SpringBootApplication
@EnableFeignClients()
public class CurrencyConversionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionServiceApplication.class, args);
	}

}