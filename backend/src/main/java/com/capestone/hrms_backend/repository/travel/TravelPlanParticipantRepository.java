package com.capestone.hrms_backend.repository.travel;

import com.capestone.hrms_backend.entity.travel.TravelPlanParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TravelPlanParticipantRepository extends JpaRepository<TravelPlanParticipant,Long> {
    List<TravelPlanParticipant> findByTravelPlanId(Long travelId);
    Optional<TravelPlanParticipant> findByTravelPlanIdAndEmployeeId(Long travelId,Long empId);
    void deleteByTravelPlanIdAndEmployeeId(Long travelId, Long empId);
    List<TravelPlanParticipant> findByEmployeeId(Long empId);
}
