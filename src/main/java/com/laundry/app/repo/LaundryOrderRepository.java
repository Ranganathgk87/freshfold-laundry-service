package com.laundry.app.repo;
import com.laundry.app.model.LaundryOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface LaundryOrderRepository extends JpaRepository<LaundryOrder,Long> {
    @EntityGraph(attributePaths={"items.service","items.clothingItem"})
    List<LaundryOrder> findByCustomerEmailOrderByCreatedAtDesc(String email);
    @Query("select distinct o from LaundryOrder o left join fetch o.customer left join fetch o.items i left join fetch i.service left join fetch i.clothingItem order by o.createdAt desc")
    List<LaundryOrder> findAllForAdmin();
    long countByStatus(String status);
}

