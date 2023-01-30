package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/*
From currency conversion, we want to call currency exchange using the currency exchange proxy. There are two things that you need to be careful about -

1) It should be an interface so interface CurrencyExchangeProxy.
2) Go to the CurrencyConversionServiceApplication class and add in @EnableFeignClients annotation there.

So once we add the dependency, we'll need to enable this, and then we can create the proxy.

And in CurrencyExchangeProxy interface, add @FeignClient annotation. And for the Feign Client I can give a name, the name we want to use in here is currency-exchange.
Typically, we would use the application name of the service that we would want to call so we've passed currency-exchange in the name of FeignClient.
And the next parameter we want to put is the url. What is the URL of the currency exchange that we want to make use of?

You don't really need to put the entire path. All that you need to put is the localhost:8000. So localhost:8000 is the URL we want to make use of.
and the URL that needs to be used to call should be part of the method definitions inside the interface.

So what service do we want to call? Let's, go to currency exchange controller. That's where our service is defined.
What I'll do is copy the method signature of the url to be used as is and paste this inside our proxy interface.
And over there and put a semicolon at end as we don't really have access to the currency exchange bean, so I'll change this to currency conversion.

You'd see that we have created the currency conversion bean matching the structure of the response of the currency exchange. And therefore, the returned values are automatically
mapped into the currency conversion bean. So the currency exchange proxy is now ready. You would see that the application starts up without a problem.
 */

//@FeignClient(name="currency-exchange", url = "localhost:8000")
/*
    For load balancing between the multiple instances of currency exchange from currency conversion, we just need to remove the url property of @FeignClient annotation
    So we want the Feign client to talk to Eureka and pick up the instances of currency exchange and do load balancing between them. All magic would happen just by removing this URL.
    Make sure that you have the currency conversion feign and the currency exchange services up and running.

    You'd see that the application would start up. So now we don't have a specific instance configured for the currency exchange.
    Let's go to currency conversion Feign and refresh. The application is still working. Now I would launch up the currency exchange service application on port 8001 as well.

    When it starts up, It would register with Eureka and if you go over to Eureka and refresh, You can see that there are two instances of currency exchange have been running
    i.e. on 8000 and 8001. And if I go and execute the Feign URL again, refresh. This is coming back from 8001, refresh 8000 refresh 8001, 8000.

    If you don't see 8,000 and 8,001 coming back, give it a little time, about 30 to 60 seconds, and then you'd see that it's automatically load balancing between these two things.
    So what is happening in here is inside the currency conversion microservice there is load balancer component which is talking to the naming server, finding the instances
    and doing automatic load balancing between them. And this is what is called client side load balancing and this is happening through Feign.

    And how does Feign do load balancing?

    If you actually go into our currency conversion service pom.xml and look at the dependency hierarchy which is present there, you'd see that there is a load balancer
    spring-cloud-starter-loadbalancer which is brought into the class path by spring-Cloud-start-up-Netflix-Eureka-client.
    And this is the load balancer framework that is used by Feign to actually distribute the load among the multiple instances which are returned by Eureka.

    In earlier versions of Spring Cloud, load balancer which was used was Ribbon and in recent versions, spring cloud shifted to using spring cloud load balancer as the load balancer.
    The great thing is if you're using Eureka and Feign then load balancing comes for free. This is client site load balancing and this comes for free for you.

    What I would recommend you to do is to stop certain instances of currency exchange and start new instances. You would see that typically within 15 to 30 seconds all the
    changes are reflected and the load balancing will be done between all the available active instances at that particular point in time. So we are creating a very dynamic structure.
 */
// @FeignClient(name="currency-exchange", url = "localhost:8010") If we pass url here then even though we have multiple instances registered with Eureka but it'll always fetch only the url mentioned here.
@FeignClient(name="currency-exchange")
public interface CurrencyExchangeProxy
{
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyConversion retrieveExchangeValue(@PathVariable String from, @PathVariable String to);
}