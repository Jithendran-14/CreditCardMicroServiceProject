package com.ofss.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "CUSTOMER_INFO")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Handles unique auto-generation
    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Enter a valid email address")
    private String emailAddress;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "\\d{10}",
        message = "Mobile number must contain exactly 10 digits"
    )
    private Long mobileNumber;

    @NotBlank(message = "PAN number is required")
    @Pattern(
        regexp = "[A-Z]{5}[0-9]{4}[A-Z]",
        message = "Enter a valid PAN format"
    )
    private String panNumber;

    // Default Constructor
    public Customer() {
    }

    // Parameterized Constructor
    public Customer(Long customerId, String customerName, String emailAddress, @NotBlank(message = "Mobile number is required") @Pattern(regexp = "\\d{10}", message = "Mobile number must contain exactly 10 digits") @NotBlank(message = "Mobile number is required") @Pattern(regexp = "\\d{10}", message = "Mobile number must contain exactly 10 digits") Long mobileNumber, String panNumber) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.panNumber = panNumber;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public @NotBlank(message = "Mobile number is required") @Pattern(regexp = "\\d{10}", message = "Mobile number must contain exactly 10 digits") Long getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(Long mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", customerName=" + customerName + ", emailAddress="
				+ emailAddress + ", mobileNumber=" + mobileNumber + ", panNumber=" + panNumber + "]";
	}
}
