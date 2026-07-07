package com.parapharmacie.repository;

import com.parapharmacie.model.CustomerOrder;
import com.parapharmacie.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    @EntityGraph(attributePaths = {"items", "user"})
    List<CustomerOrder> findByUserOrderByCreatedAtDesc(User user);

    @Override
    @EntityGraph(attributePaths = {"items", "user"})
    List<CustomerOrder> findAll();
}
