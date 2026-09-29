package com.ofss.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ofss.entity.Merchant;
import com.ofss.repository.MerchantRepository;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private MerchantService merchantService;

    private Merchant merchant;

    @BeforeEach
    void setUp() {

        merchant = new Merchant();

        merchant.setMerchantId(1L);
        merchant.setMerchantName("Amazon");
        merchant.setCategory("E-Commerce");
        merchant.setLocation("Bangalore");
    }

    // ---------------------------------------------------------
    // 1. ADD MERCHANT - SUCCESS
    // ---------------------------------------------------------
    @Test
    void addAMerchant_ShouldSaveAndReturnMerchant() {

        when(merchantRepository.save(merchant))
                .thenReturn(merchant);

        Merchant result =
                merchantService.addAMerchant(merchant);

        assertNotNull(result);
        assertEquals(1L, result.getMerchantId());
        assertEquals("Amazon", result.getMerchantName());
        assertEquals("E-Commerce", result.getCategory());
        assertEquals("Bangalore", result.getLocation());

        verify(merchantRepository, times(1))
                .save(merchant);
    }

    // ---------------------------------------------------------
    // 2. LIST ALL MERCHANTS
    // ---------------------------------------------------------
    @Test
    void listAllMerchants_ShouldReturnAllMerchants() {

        Merchant merchant2 = new Merchant();

        merchant2.setMerchantId(2L);
        merchant2.setMerchantName("Flipkart");
        merchant2.setCategory("E-Commerce");
        merchant2.setLocation("Mumbai");

        when(merchantRepository.findAll())
                .thenReturn(Arrays.asList(merchant, merchant2));

        List<Merchant> result =
                merchantService.listAllMerchants();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Amazon",
                result.get(0).getMerchantName());

        assertEquals("Flipkart",
                result.get(1).getMerchantName());

        verify(merchantRepository, times(1))
                .findAll();
    }

    // ---------------------------------------------------------
    // 3. LIST ALL MERCHANTS - EMPTY
    // ---------------------------------------------------------
    @Test
    void listAllMerchants_ShouldReturnEmptyList_WhenNoMerchantsExist() {

        when(merchantRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<Merchant> result =
                merchantService.listAllMerchants();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(merchantRepository, times(1))
                .findAll();
    }

    // ---------------------------------------------------------
    // 4. GET MERCHANT BY ID - SUCCESS
    // ---------------------------------------------------------
    @Test
    void getMerchantById_ShouldReturnMerchant_WhenMerchantExists() {

        when(merchantRepository.findById(1L))
                .thenReturn(Optional.of(merchant));

        Optional<Merchant> result =
                merchantService.getMerchantById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L,
                result.get().getMerchantId());
        assertEquals("Amazon",
                result.get().getMerchantName());

        verify(merchantRepository, times(1))
                .findById(1L);
    }

    // ---------------------------------------------------------
    // 5. GET MERCHANT BY ID - NOT FOUND
    // ---------------------------------------------------------
    @Test
    void getMerchantById_ShouldReturnEmpty_WhenMerchantDoesNotExist() {

        when(merchantRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Merchant> result =
                merchantService.getMerchantById(99L);

        assertTrue(result.isEmpty());

        verify(merchantRepository, times(1))
                .findById(99L);
    }

    // ---------------------------------------------------------
    // 6. UPDATE MERCHANT - SUCCESS
    // ---------------------------------------------------------
    @Test
    void updateMerchant_ShouldUpdateAndReturnMerchant_WhenMerchantExists() {

        Merchant updatedMerchant = new Merchant();

        updatedMerchant.setMerchantName("Amazon India");
        updatedMerchant.setCategory("Online Shopping");
        updatedMerchant.setLocation("Hyderabad");

        when(merchantRepository.findById(1L))
                .thenReturn(Optional.of(merchant));

        when(merchantRepository.save(any(Merchant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Merchant> result =
                merchantService.updateMerchant(
                        1L,
                        updatedMerchant
                );

        assertTrue(result.isPresent());

        Merchant updated = result.get();

        assertEquals(1L,
                updated.getMerchantId());

        assertEquals("Amazon India",
                updated.getMerchantName());

        assertEquals("Online Shopping",
                updated.getCategory());

        assertEquals("Hyderabad",
                updated.getLocation());

        verify(merchantRepository, times(1))
                .findById(1L);

        verify(merchantRepository, times(1))
                .save(merchant);
    }

    // ---------------------------------------------------------
    // 7. UPDATE MERCHANT - NOT FOUND
    // ---------------------------------------------------------
    @Test
    void updateMerchant_ShouldReturnEmpty_WhenMerchantDoesNotExist() {

        Merchant updatedMerchant = new Merchant();

        updatedMerchant.setMerchantName("Amazon India");
        updatedMerchant.setCategory("Online Shopping");
        updatedMerchant.setLocation("Hyderabad");

        when(merchantRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Merchant> result =
                merchantService.updateMerchant(
                        99L,
                        updatedMerchant
                );

        assertTrue(result.isEmpty());

        verify(merchantRepository, times(1))
                .findById(99L);

        verify(merchantRepository, never())
                .save(any(Merchant.class));
    }

    // ---------------------------------------------------------
    // 8. DELETE MERCHANT - SUCCESS
    // ---------------------------------------------------------
    @Test
    void deleteMerchant_ShouldReturnTrue_WhenMerchantExists() {

        when(merchantRepository.existsById(1L))
                .thenReturn(true);

        boolean result =
                merchantService.deleteMerchant(1L);

        assertTrue(result);

        verify(merchantRepository, times(1))
                .existsById(1L);

        verify(merchantRepository, times(1))
                .deleteById(1L);
    }

    // ---------------------------------------------------------
    // 9. DELETE MERCHANT - NOT FOUND
    // ---------------------------------------------------------
    @Test
    void deleteMerchant_ShouldReturnFalse_WhenMerchantDoesNotExist() {

        when(merchantRepository.existsById(99L))
                .thenReturn(false);

        boolean result =
                merchantService.deleteMerchant(99L);

        assertFalse(result);

        verify(merchantRepository, times(1))
                .existsById(99L);

        verify(merchantRepository, never())
                .deleteById(anyLong());
    }
}