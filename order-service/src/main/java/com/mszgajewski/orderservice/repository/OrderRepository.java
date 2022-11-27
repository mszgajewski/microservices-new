package com.mszgajewski.orderservice.repository;

import com.mszgajewski.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
