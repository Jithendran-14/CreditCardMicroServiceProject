package com.ofss.client;

import com.ofss.dto.MerchantResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class MerchantClient {

    private final RestTemplate restTemplate;

    public MerchantClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MerchantResponse getMerchantById(Long merchantId) {

        try {

            return restTemplate.getForObject(
                    "http://merchant-management/merchants/{id}",
                    MerchantResponse.class,
                    merchantId
            );

        } catch (RestClientException exception) {

            return null;
        }
    }
}

