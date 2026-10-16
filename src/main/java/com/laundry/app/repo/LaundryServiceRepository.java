package com.laundry.app.repo;
import com.laundry.app.model.LaundryService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LaundryServiceRepository extends JpaRepository<LaundryService,Long> {
    List<LaundryService> findByActiveTrueOrderByNameAsc();
}
