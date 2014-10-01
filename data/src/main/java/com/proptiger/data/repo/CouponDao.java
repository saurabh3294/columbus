package com.proptiger.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proptiger.data.model.Coupon;

public interface CouponDao extends JpaRepository<Coupon, Integer> {

}
