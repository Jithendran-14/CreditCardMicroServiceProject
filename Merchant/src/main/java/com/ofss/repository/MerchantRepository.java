package com.ofss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofss.entity.Merchant;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

}