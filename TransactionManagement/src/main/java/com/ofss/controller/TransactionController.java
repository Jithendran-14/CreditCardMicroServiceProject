
package com.ofss.controller;

import com.ofss.dto.PaymentRequest;
import java.math.BigDecimal;
import com.ofss.dto.PurchaseRequest;
import com.ofss.entity.Transaction;
import com.ofss.service.TransactionService;
import org.springframework.web.bind.annotation.RequestParam;
import com.ofss.dto.CreditCardStatement;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;


    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }


    // ---------------------------------------
    // Get all transactions
    // ---------------------------------------

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }


    // ---------------------------------------
    // Get transaction by ID
    // ---------------------------------------

    @GetMapping("/id/{id}")
    public ResponseEntity<Transaction> getTransactionById(
            @PathVariable Long id) {

        Transaction transaction =
                transactionService.getTransactionById(id);

        if (transaction == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(transaction);
    }


    // ---------------------------------------
    // Purchase
    // ---------------------------------------

    @PostMapping("/purchase")
    public ResponseEntity<Transaction> processPurchase(
            @Valid @RequestBody PurchaseRequest request) {

        Transaction transaction =
                transactionService.processPurchase(
                        request
                );


        if ("FAILED".equals(
                transaction.getTransactionStatus())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(transaction);
        }


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }


    // ---------------------------------------
    // Payment
    // ---------------------------------------

    @PostMapping("/payment")
    public ResponseEntity<Transaction> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        Transaction transaction =
                transactionService.processPayment(
                        request
                );


        if ("FAILED".equals(
                transaction.getTransactionStatus())) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(transaction);
        }


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }
    


	@GetMapping("/card/{cardId}")
	public ResponseEntity<List<Transaction>> getTransactionsByCard(
	        @PathVariable Long cardId) {
	
	    return ResponseEntity.ok(
	            transactionService.getTransactionsByCardId(cardId)
	    );
	}
	
	@GetMapping("/type/{type}")
	public ResponseEntity<List<Transaction>> getTransactionsByType(
	        @PathVariable String type) {
	
	    return ResponseEntity.ok(
	            transactionService.getTransactionsByType(type)
	    );
	}
	@GetMapping("/status/{status}")
	public ResponseEntity<List<Transaction>> getTransactionsByStatus(
	        @PathVariable String status) {
	
	    return ResponseEntity.ok(
	            transactionService.getTransactionsByStatus(status)
	    );
	}
	
	@GetMapping("/merchant/{merchantId}")
	public ResponseEntity<List<Transaction>> getTransactionsByMerchant(
	        @PathVariable Long merchantId) {
	
	    return ResponseEntity.ok(
	            transactionService.getTransactionsByMerchantId(merchantId)
	    );
	}
	
	
	@GetMapping("/date-range")
	public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
	        @RequestParam LocalDateTime from,
	        @RequestParam LocalDateTime to) {
	
	    return ResponseEntity.ok(
	            transactionService.getTransactionsByDateRange(from, to)
	    );
	}
	@GetMapping("/search")
	public ResponseEntity<List<Transaction>> searchTransactions(
	        @RequestParam(required = false) Long cardId,
	        @RequestParam(required = false) String type,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) Long merchantId,
	        @RequestParam(required = false) LocalDateTime from,
	        @RequestParam(required = false) LocalDateTime to) {
	
	    return ResponseEntity.ok(
	            transactionService.searchTransactions(
	                    cardId,
	                    type,
	                    status,
	                    merchantId,
	                    from,
	                    to
	            )
	    );
	}
	
	@GetMapping("/amount/{amount}")
	public ResponseEntity<List<Transaction>>
	getTransactionsByAmount(
	        @PathVariable BigDecimal amount) {

	    return ResponseEntity.ok(
	            transactionService.getTransactionsByAmount(
	                    amount
	            )
	    );
	}
	
	@GetMapping("/card/{cardId}/statement")
	public ResponseEntity<CreditCardStatement>
	generateStatement(
	        @PathVariable Long cardId,
	        @RequestParam(required = false) LocalDateTime from,
	        @RequestParam(required = false) LocalDateTime to) {

	    return ResponseEntity.ok(
	            transactionService.generateStatement(
	                    cardId,
	                    from,
	                    to
	            )
	    );
	}

}






