package com.ofss.client;


import com.ofss.dto.CreditCardResponse;
import com.ofss.dto.PaymentRequest;
import com.ofss.dto.PurchaseRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class CreditCardClient {

    private final RestTemplate restTemplate;


    public CreditCardClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // ---------------------------------------
    // Get Credit Card
    // ---------------------------------------

    public CreditCardResponse getCreditCardById(Long cardId) {

        try {

            return restTemplate.getForObject(
                    "http://credit-card-management/cards/id/{id}",
                    CreditCardResponse.class,
                    cardId
            );

        } catch (RestClientException exception) {

            return null;
        }
    }


    // ---------------------------------------
    // Process Purchase
    // ---------------------------------------

    public CreditCardResponse processPurchase(
            Long cardId,
            BigDecimal amount) {

        PurchaseRequest request =
                new PurchaseRequest();

        request.setAmount(amount);


        try {

            ResponseEntity<CreditCardResponse> response =
                    restTemplate.exchange(
                            "http://credit-card-management/cards/id/{id}/purchase",
                            HttpMethod.PUT,
                            new HttpEntity<>(request),
                            CreditCardResponse.class,
                            cardId
                    );

            return response.getBody();

        } catch (HttpStatusCodeException exception) {

            String errorMessage =
                    exception.getResponseBodyAsString();

            throw new IllegalArgumentException(
                    errorMessage
            );

        } catch (RestClientException exception) {

            throw new IllegalArgumentException(
                    "Unable to communicate with Credit Card Management service"
            );
        }
    }


    // ---------------------------------------
    // Process Payment
    // ---------------------------------------

    public CreditCardResponse processPayment(
            Long cardId,
            BigDecimal amount) {

        PaymentRequest request =
                new PaymentRequest();

        request.setAmount(amount);


        try {

            ResponseEntity<CreditCardResponse> response =
                    restTemplate.exchange(
                            "http://credit-card-management/cards/id/{id}/payment",
                            HttpMethod.PUT,
                            new HttpEntity<>(request),
                            CreditCardResponse.class,
                            cardId
                    );

            return response.getBody();

        } catch (HttpStatusCodeException exception) {

            String errorMessage =
                    exception.getResponseBodyAsString();

            throw new IllegalArgumentException(
                    errorMessage
            );

        } catch (RestClientException exception) {

            throw new IllegalArgumentException(
                    "Unable to communicate with Credit Card Management service"
            );
        }
    }
}
