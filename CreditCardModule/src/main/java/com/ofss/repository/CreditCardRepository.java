
package com.ofss.repository;

import java.util.List;
import com.ofss.dto.CustomerOutstandingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ofss.entity.CreditCard;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    boolean existsByCardNumber(String cardNumber);

    List<CreditCard> findByCustomerId(Long customerId);
    


@Query("""
       SELECT COALESCE(SUM(c.outstandingAmount), 0)
       FROM CreditCard c
       """)
BigDecimal getTotalOutstandingAmount();


@Query("""
       SELECT c
       FROM CreditCard c
       ORDER BY c.outstandingAmount DESC
       """)
List<CreditCard> findCardsOrderByOutstandingDesc();

@Query("""
       SELECT c
       FROM CreditCard c
       ORDER BY c.outstandingAmount ASC
       """)
List<CreditCard> findCardsOrderByOutstandingAsc();
@Query("""
	       SELECT c
	       FROM CreditCard c
	       WHERE c.availableCredit <= (c.creditLimit * 0.20)
	       ORDER BY c.availableCredit ASC
	       """)
	List<CreditCard> findCardsWithLowAvailableCredit();
@Query("""
	       SELECT c.customerId AS customerId,
	              SUM(c.outstandingAmount) AS totalOutstandingAmount
	       FROM CreditCard c
	       GROUP BY c.customerId
	       ORDER BY SUM(c.outstandingAmount) DESC
	       """)
	List<CustomerOutstandingReport> findCustomersByOutstandingDescending();

	@Query("""
	       SELECT c.customerId AS customerId,
	              SUM(c.outstandingAmount) AS totalOutstandingAmount
	       FROM CreditCard c
	       GROUP BY c.customerId
	       ORDER BY SUM(c.outstandingAmount) ASC
	       """)
	List<CustomerOutstandingReport> findCustomersByOutstandingAscending();

	@Query("""
	       SELECT c
	       FROM CreditCard c
	       WHERE UPPER(c.cardStatus) = 'BLOCKED'
	       """)
	List<CreditCard> findBlockedCards();

	@Query("""
		       SELECT c
		       FROM CreditCard c
		       ORDER BY c.availableCredit DESC
		       """)
		List<CreditCard> findCardsOrderByAvailableCreditDesc();

		@Query("""
		       SELECT c
		       FROM CreditCard c
		       ORDER BY c.availableCredit ASC
		       """)
		List<CreditCard> findCardsOrderByAvailableCreditAsc();


}

