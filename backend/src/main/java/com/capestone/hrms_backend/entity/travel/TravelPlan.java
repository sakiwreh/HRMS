package com.capestone.hrms_backend.entity.travel;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.shared.Base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private Employee hr;

    private String title;

    private String description;
    private String destination;
    @Column(name = "is_cancelled")
    private boolean cancelled = false;

    @Column(name = "departure_date")
    private LocalDateTime depatureDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    //ManyToMany
    @ManyToMany
    @JoinTable(
            name = "travel_plan_participants",
            joinColumns = @JoinColumn(name = "travel_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees;
}

