package com.ofss.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ofss.entity.Merchant;
import com.ofss.repository.MerchantRepository;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    // Add a new merchant
    public Merchant addAMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    // Get all merchants
    public List<Merchant> listAllMerchants() {
        return merchantRepository.findAll();
    }

    // Get merchant by ID
    public Optional<Merchant> getMerchantById(Long merchantId) {
        return merchantRepository.findById(merchantId);
    }

    // Update merchant
    public Optional<Merchant> updateMerchant(Long merchantId, Merchant merchant) {

        Optional<Merchant> existingMerchant =
                merchantRepository.findById(merchantId);

        if (existingMerchant.isPresent()) {

            Merchant existing = existingMerchant.get();

            existing.setMerchantName(merchant.getMerchantName());
            existing.setCategory(merchant.getCategory());
            existing.setLocation(merchant.getLocation());

            return Optional.of(merchantRepository.save(existing));
        }

        return Optional.empty();
    }

    // Delete merchant
    public boolean deleteMerchant(Long merchantId) {

        if (merchantRepository.existsById(merchantId)) {

            merchantRepository.deleteById(merchantId);

            return true;
        }

        return false;
    }
}