package com.capestone.hrms_backend.entity.travel;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "travel_plans")
@Getter
@Setter
public class TravelPlan extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",nullable = false)
    private Employee createdBy;

    @Column(nullable = false,length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false,length = 180)
    private String destination;


    @Column(name = "is_cancelled",nullable = false)
    private boolean cancelled = false;

    @Column(name="max_per_day_amount", precision = 12, scale = 2)
    private BigDecimal maxPerDayAmount;

    @Column(name = "departure_date",nullable = false)
    private LocalDateTime depatureDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;
}

