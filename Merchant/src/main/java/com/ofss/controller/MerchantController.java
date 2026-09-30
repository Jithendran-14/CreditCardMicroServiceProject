package com.ofss.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofss.entity.Merchant;
import com.ofss.service.MerchantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // Add a merchant
    @PostMapping
    public ResponseEntity<Merchant> insertAMerchant(
            @Valid @RequestBody Merchant merchant) {

        Merchant savedMerchant = merchantService.addAMerchant(merchant);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedMerchant);
    }

    // Get all merchants
    @GetMapping
    public ResponseEntity<List<Merchant>> listAllMerchants() {

        List<Merchant> merchants = merchantService.listAllMerchants();

        return ResponseEntity.ok(merchants);
    }

    // Get merchant by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMerchantById(
            @PathVariable("id") Long merchantId) {

        Optional<Merchant> merchant =
                merchantService.getMerchantById(merchantId);

        if (merchant.isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(merchant.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("No such merchant id exists");
    }

    // Update merchant
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchant(
            @PathVariable("id") Long merchantId,
            @Valid @RequestBody Merchant merchant) {

        Optional<Merchant> updatedMerchant =
                merchantService.updateMerchant(merchantId, merchant);

        if (updatedMerchant.isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(updatedMerchant.get());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("No such merchant id exists");
    }

    // Delete merchant
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMerchant(
            @PathVariable("id") Long merchantId) {

        boolean deleted = merchantService.deleteMerchant(merchantId);

        if (deleted) {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Merchant deleted successfully");
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("No such merchant id exists");
    }
}