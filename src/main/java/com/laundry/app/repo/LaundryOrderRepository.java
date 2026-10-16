package com.laundry.app.repo;
import com.laundry.app.model.LaundryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LaundryOrderRepository extends JpaRepository<LaundryOrder,Long> {
    List<LaundryOrder> findByCustomerEmailOrderByCreatedAtDesc(String email);
    long countByStatus(String status);
}
