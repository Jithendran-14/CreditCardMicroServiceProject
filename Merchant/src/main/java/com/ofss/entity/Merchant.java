package com.ofss.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "MERCHANT_INFO")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MERCHANT_ID")
    private Long merchantId;

    @NotBlank(message = "Merchant name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Merchant name must be between 2 and 100 characters"
    )
    private String merchantName;

    @NotBlank(message = "Merchant category is required")
    @Size(
        max = 50,
        message = "Merchant category cannot exceed 50 characters"
    )
    private String category;

    @NotBlank(message = "Merchant location is required")
    @Size(
        max = 150,
        message = "Merchant location cannot exceed 150 characters"
    )
    private String location;

    // Default constructor required by JPA
    public Merchant() {
    }

    // Parameterized constructor
    public Merchant(String merchantName, String category, String location) {
        this.merchantName = merchantName;
        this.category = category;
        this.location = location;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "merchantId=" + merchantId +
                ", merchantName='" + merchantName + '\'' +
                ", category='" + category + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
