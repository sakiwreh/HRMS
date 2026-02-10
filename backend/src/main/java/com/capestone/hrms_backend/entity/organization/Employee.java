package com.capestone.hrms_backend.entity.organization;

import com.capestone.hrms_backend.entity.shared.Base;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50, name = "first_name")
    private String firstName;

    @Column(length = 50, name = "middle_name")
    private String middleName;

    @Column(length = 50, name = "last_name")
    private String lastName;

    @Column(length = 50)
    private String designation;


    private LocalDate dob;
    private LocalDate doj;

    //M:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    //M:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;

    //M:1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "profile_pic_path")
    private String profilePath;

    //ManyToMany
    @ManyToMany(mappedBy = "employees")
    private Set<TravelPlan> travelPlans;
}