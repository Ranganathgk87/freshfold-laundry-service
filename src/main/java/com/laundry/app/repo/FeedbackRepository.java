package com.laundry.app.repo;
import com.laundry.app.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    boolean existsByLaundryOrderId(Long orderId);
    List<Feedback> findAllByOrderByCreatedAtDesc();
}
