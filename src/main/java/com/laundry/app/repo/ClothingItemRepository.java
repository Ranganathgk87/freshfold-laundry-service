package com.laundry.app.repo;
import com.laundry.app.model.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClothingItemRepository extends JpaRepository<ClothingItem,Long> {
    List<ClothingItem> findByActiveTrueOrderByNameAsc();
}
