package com.ofss.controller;

import java.util.List;
import com.ofss.dto.CustomerOutstandingReport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ofss.dto.PurchaseRequest;
import com.ofss.entity.CreditCard;
import com.ofss.service.CreditCardService;
import com.ofss.dto.PaymentRequest;
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

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCard);
    }

    @GetMapping
    public ResponseEntity<List<CreditCard>> getAllCards() {
        return ResponseEntity.ok(creditCardService.getAllCards());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CreditCard> getCardById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                creditCardService.getCardById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditCard>> getCardsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                creditCardService.getCardsByCustomerId(customerId));
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<CreditCard> updateCard(
            @PathVariable Long id,
            @RequestBody CreditCard card) {

        return ResponseEntity.ok(
                creditCardService.updateCard(id, card));
    }

    @PutMapping("/id/{id}/status")
    public ResponseEntity<CreditCard> updateCardStatus(
            @PathVariable Long id,
            @RequestBody CreditCard card) {

        return ResponseEntity.ok(
                creditCardService.updateCardStatus(id, card));
    }
    
    @PutMapping("/id/{id}/purchase")
    public ResponseEntity<CreditCard> processPurchase(
            @PathVariable Long id,
            @RequestBody PurchaseRequest request) {

        CreditCard updatedCard =
                creditCardService.processPurchase(id, request.getAmount());

        return ResponseEntity.ok(updatedCard);
    }
    @PutMapping("/id/{id}/payment")
    public ResponseEntity<CreditCard> processPayment(
            @PathVariable Long id,
            @RequestBody PaymentRequest request) {

        CreditCard updatedCard =
                creditCardService.processPayment(
                        id,
                        request.getAmount()
                );

        return ResponseEntity.ok(updatedCard);
    }
 // =========================================================
 // REPORT: CUSTOMER WITH HIGHEST OUTSTANDING
 // =========================================================
 @GetMapping("/reports/customer/highest-outstanding")
 public ResponseEntity<CustomerOutstandingReport>
         getCustomerWithHighestOutstanding() {

     return ResponseEntity.ok(
             creditCardService.getCustomerWithHighestOutstanding()
     );
 }


 // =========================================================
 // REPORT: CUSTOMER WITH LOWEST OUTSTANDING
 // =========================================================
 @GetMapping("/reports/customer/lowest-outstanding")
 public ResponseEntity<CustomerOutstandingReport>
         getCustomerWithLowestOutstanding() {

     return ResponseEntity.ok(
             creditCardService.getCustomerWithLowestOutstanding()
     );
 }


}