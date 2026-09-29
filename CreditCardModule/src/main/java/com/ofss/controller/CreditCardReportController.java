package com.ofss.controller;

import com.ofss.entity.CreditCard;
import com.ofss.service.CreditCardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/reports/cards")
public class CreditCardReportController {

    private final CreditCardService creditCardService;

    public CreditCardReportController(
            CreditCardService creditCardService) {

        this.creditCardService = creditCardService;
    }

    @GetMapping("/total-outstanding")
    public ResponseEntity<BigDecimal> getTotalOutstanding() {

        return ResponseEntity.ok(
                creditCardService.getTotalOutstandingAmount()
        );
    }

    @GetMapping("/highest-outstanding")
    public ResponseEntity<CreditCard> getHighestOutstandingCard() {

        CreditCard card =
                creditCardService.getHighestOutstandingCard();

        if (card == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(card);
    }

    @GetMapping("/lowest-outstanding")
    public ResponseEntity<CreditCard> getLowestOutstandingCard() {

        CreditCard card =
                creditCardService.getLowestOutstandingCard();

        if (card == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(card);
    }

    @GetMapping("/low-available-credit")
    public ResponseEntity<List<CreditCard>>
    getLowAvailableCreditCards() {

        return ResponseEntity.ok(
                creditCardService.getLowAvailableCreditCards()
        );
    }

    @GetMapping("/blocked")
    public ResponseEntity<List<CreditCard>>
    getBlockedCards() {

        return ResponseEntity.ok(
                creditCardService.getBlockedCards()
        );
    }
    @GetMapping("/customer-mapping")
    public ResponseEntity<List<CreditCard>>
    getCardCustomerMapping() {

        return ResponseEntity.ok(
                creditCardService.getAllCards()
        );
    }
}