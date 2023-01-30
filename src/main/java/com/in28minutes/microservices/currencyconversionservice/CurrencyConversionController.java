package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
    /*
    Let's create a simple REST API in the currency conversion service as well.

    The REST API that we want to create is of this form - localhost:8100/currency-conversion/from/<one-currency>/to/<another-currency>/quantity/<quantity>.
    What we want to do in the currency conversion service is invoke the currency exchange micro and do the calculation.
    So we've created this new Controller, CurrencyConversionController. And added in a method here for exposing rest api with specified url.

    Example URL and Response - http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10
    {
      "id": 10001,
      "from": "USD",
      "to": "INR",
      "conversionMultiple": 65.00,
      "quantity": 10,
      "totalCalculatedAmount": 650.00,
      "environment": "8000 instance-id"
    }

    The details here in CurrencyConversion would be the same as what we had in CurrencyExchange.

    So we'll have id, from, to, conversionMultiple, quantity, environment and totalCalculatedAmount which is not present in the currency exchange bean,
    We generate constructors along with getter and setter methods for all the attributes.

    We would also create a no argument constructor, just to be safe. And I'll go to the Currency Conversion Controller and initially return a hard coded value.

    So we'll have a working API after above things. Cool, I'm getting the information back from/USD/to/INR/quantity/10.
    And all are hardcoded for now. So the last 3 variables and the ID are also hardcoded in here.

    Next, we'll fix that. We'll talk to the currency exchange micro and will get the details back from currency exchange micro and publish it as part of this API.
 */
@RestController // We would want this to be a REST controller.
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity)
    {
        HashMap<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

    /*
    In this step, let's call the currency exchange micro from the currency conversion micro.
    We want to call the currency exchange micro from here and to do that, use RestTemplate which can be used to make rest API calls.

    So create a new RestTemplate and use method getForEntity. We want to send a GET request and want to get an object back.
    That's why we are using the getForEntity method of RestTemplate.

    And the first argument is what is the URL that you'd want to work which we've passed there.
    Make sure that passed URL is working i.e. currency exchange service should be up and running with this url - localhost:8000/currency-exchange/from/USD/to/INR.
    I'll hardcode this url for now. Later, we'll see how to make this dynamic. For now just try and get it working.

    If we don't want to hardcode the from (USD) and to (INR) values then pass in from and to variable inside {} bracket, so whatever is in the path variables,
    I would want to pick that up and pass it as part of from and to. The return response should be converted into CurrencyConversion structure so pass
    CurrencyConversion.class as second argument to getForEntity method. Note that the currency conversion structure matches the response of the currency exchange
    micro and therefore those values automatically get mapped. As part of the currency conversion service, we'll add in the quantity and also calculate
    the totalCalculatedAmount extra here.

    The next thing that we would need to pass in is what are the values for from and to. To pass in the values of from and to, we create something called URI variables.

    So we need to pass in another parameter called URI variables which is nothing but a hashmap. So we create local variable uriVariables of HashMap<String, String> type
    and create a new HashMap. Now add the values into the uriVariables using uriVariables.put where from is the key and the value which you add in is from. Similarly, we add in to.

    As you can see there is a lot of work to be done to make a simple rest API call. We'll see how to simplify that a little later.

    So uriVariables are now mapped and everything looks good. Now we would want to assign the new RestTemplate created above to a new local variable and
    this would return a ResponseEntity back. The way we get the return value from the response entity is by saying responseEntity.getBody() which we'll map it
    to a new local variable currencyConversion of type CurrencyConversion.

    And we want to return a new currency conversion using the values which are coming in above currencyConversion variable from responseEntity.getBody().
    And we calculate the total value by multiplication of conversionMultiple with the quantity.

    After that we compile it and no problems there. And we start the url - http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10
    Cool. I can see that there is a response coming back. So the quantity is 10, the conversion multiple is 65 and the total calculated amount is 650.

    And the environment, the port where it's getting the response back from is 8000. So this looks good. After a lot of hard work, we are now ready with the
    currency conversion micro and the currency exchange micro and we are now ready to explore most of the wonderful things around spring cloud.
    */
        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class, uriVariables);

        CurrencyConversion currencyConversion = responseEntity.getBody();

        return new CurrencyConversion(currencyConversion.getId(), from, to, quantity, currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()), currencyConversion.getEnvironment() + " " + "rest template");
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity)
    {
    /*
    Above, we had to write a lot of tedious code around RestTemplate to get the currency conversion service to talk with the currency exchange micro.

    To make a simple rest API call, we need to write about 20 lines of code and imagine what would happen if in a micros architecture,
    you have hundreds of micros. They'd be calling each other and you need to repeat this kind of code everywhere.

    And that's where Spring Cloud provides you with a framework called Feign. Feign makes it really easy to call other micros.
    And to make use of Feign, we need to add a specific dependency into our currency conversion service with org.springframework.cloud as groupId and spring-cloud-starter-openfeign
    as the artifact ID for the integration of Spring with open feign framework. So all that we need to do to make use of Feign is to add this dependency in pom.xml.

    Now we would want to be able to talk to the currency exchange service from the currency conversion controller. And to be able to do that, we need to do is to create a proxy.
    So we've created a new interface named CurrencyExchangeProxy in currency conversion service as from currency conversion service, we want to call the currency exchange.

    And now we would want to make use of the currency exchange proxy in our currency conversion controller.
    The method which is using direct call using RestTemplate, I'll leave it as it is, and I'll create a new method to update it to Feign.

    So the method signature remains the same, except that where we created a ResponseEntity and everything, we don't want to do that.
    We want to directly use the CurrencyExchangeProxy type variable and autowire it in. And make currencyConversion = proxy.retrieveExchangeValue(from, to);

    So we eliminated a lot of lines of code by using Feign. Try new URL that we have created currency-conversion-feign and you can see that it also is working.
    As you can see, Feign is very important when we talk about micros because it makes it very easy for us to call rest API.
    A little later, when we add the naming server into the picture, you would see that Feign also helps us to do load balancing very easily.
    */
        CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

        return new CurrencyConversion(currencyConversion.getId(), from, to, quantity, currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()), currencyConversion.getEnvironment()+ " " + "feign");
    }
}