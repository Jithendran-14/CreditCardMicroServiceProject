package com.ofss.specification;

import com.ofss.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    public static Specification<Transaction> hasCardId(Long cardId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("cardId"), cardId);
    }

    public static Specification<Transaction> hasTransactionType(
            String transactionType) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("transactionType"),
                        transactionType
                );
    }

    public static Specification<Transaction> hasTransactionStatus(
            String transactionStatus) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("transactionStatus"),
                        transactionStatus
                );
    }

    public static Specification<Transaction> hasMerchantId(Long merchantId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("merchantId"), merchantId);
    }

    public static Specification<Transaction> dateGreaterThanOrEqual(
            LocalDateTime from) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("transactionDate"),
                        from
                );
    }

    public static Specification<Transaction> dateLessThanOrEqual(
            LocalDateTime to) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("transactionDate"),
                        to
                );
    }

    private TransactionSpecification() {
    }
}


