package com.capestone.hrms_backend.entity.travel;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import com.capestone.hrms_backend.entity.shared.Document;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Fetch;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "travel_documents")
@Getter
@Setter
public class TravelDocument extends Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //M:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id",nullable = false)
    private TravelPlan travelPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee uploadedFor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by",nullable = false)
    private Employee uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocType docType;
}
