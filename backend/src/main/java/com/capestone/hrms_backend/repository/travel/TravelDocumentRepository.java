package com.capestone.hrms_backend.repository.travel;

import com.capestone.hrms_backend.entity.travel.TravelDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelDocumentRepository extends JpaRepository<TravelDocument,Long> {
    List<TravelDocument> findByTravelPlanId(Long travelId);
}