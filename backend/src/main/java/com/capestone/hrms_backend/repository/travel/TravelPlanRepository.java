package com.capestone.hrms_backend.repository.travel;

import com.capestone.hrms_backend.entity.travel.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan,Long> {
    List<TravelPlan> findByCreatedByIdOrderByDepatureDateDesc(Long hrId);
}
