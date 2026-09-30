 
package com.ofss.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ofss.dto.CustomerResponse;

@Component
public class CustomerClient {

    private final RestTemplate restTemplate;

    public CustomerClient(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    public CustomerResponse getCustomerById(Long customerId) {

        try {

            return restTemplate.getForObject(
                    "http://customer-management/customers/id/{id}",
                    CustomerResponse.class,
                    customerId
            );

        } catch (RestClientException exception) {

            return null;
        }
    }
}