package com.ofss.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ofss.dto.CustomerOutstandingReport;
import com.ofss.dto.PaymentRequest;
import com.ofss.dto.PurchaseRequest;
import com.ofss.entity.CreditCard;
import com.ofss.service.CreditCardService;

@RestController
@RequestMapping("/cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping
    public ResponseEntity<CreditCard> issueCard(
            @RequestBody CreditCard card) {

        CreditCard savedCard = creditCardService.issueCard(card);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

    @GetMapping
    public ResponseEntity<List<CreditCard>> getAllCards() {
        return ResponseEntity.ok(creditCardService.getAllCards());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CreditCard> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(creditCardService.getCardById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditCard>> getCardsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                creditCardService.getCardsByCustomerId(customerId)
        );
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<CreditCard> updateCard(
            @PathVariable Long id,
            @RequestBody CreditCard card) {

        return ResponseEntity.ok(
                creditCardService.updateCard(id, card)
        );
    }

    @PutMapping("/id/{id}/status")
    public ResponseEntity<CreditCard> updateCardStatus(
            @PathVariable Long id,
            @RequestBody CreditCard card) {

        return ResponseEntity.ok(
                creditCardService.updateCardStatus(id, card)
        );
    }

    @PutMapping("/id/{id}/purchase")
    public ResponseEntity<CreditCard> processPurchase(
            @PathVariable Long id,
            @RequestBody PurchaseRequest request) {

        return ResponseEntity.ok(
                creditCardService.processPurchase(id, request.getAmount())
        );
    }

    @PutMapping("/id/{id}/payment")
    public ResponseEntity<CreditCard> processPayment(
            @PathVariable Long id,
            @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                creditCardService.processPayment(id, request.getAmount())
        );
    }

    @GetMapping("/reports/customer/highest-outstanding")
    public ResponseEntity<List<CustomerOutstandingReport>>
            getCustomersWithHighestOutstanding() {

        return ResponseEntity.ok(
                creditCardService.getCustomersWithHighestOutstanding()
        );
    }

    @GetMapping("/reports/customer/lowest-outstanding")
    public ResponseEntity<List<CustomerOutstandingReport>>
            getCustomersWithLowestOutstanding() {

        return ResponseEntity.ok(
                creditCardService.getCustomersWithLowestOutstanding()
        );
    }
    
    @GetMapping("/reports/cards/highest-available-credit")
    public ResponseEntity<List<CreditCard>>
            getCardsWithHighestAvailableCredit() {

        return ResponseEntity.ok(
                creditCardService.getCardsWithHighestAvailableCredit()
        );
    }

    @GetMapping("/reports/cards/lowest-available-credit")
    public ResponseEntity<List<CreditCard>>
            getCardsWithLowestAvailableCredit() {

        return ResponseEntity.ok(
                creditCardService.getCardsWithLowestAvailableCredit()
        );
    }
}