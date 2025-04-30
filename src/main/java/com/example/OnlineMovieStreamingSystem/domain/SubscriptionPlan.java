package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String features;
    private boolean isActive;
    private Instant createAt;
    private Instant updateAt;

//    @OneToMany(mappedBy = "parentSubscriptionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<SubscriptionPlan> childSubscriptionPlans;
//
//    @ManyToOne
//    @JoinColumn(name="parent_subscription_plan_id")
//    private SubscriptionPlan parentSubscriptionPlan;
    @ManyToMany
    @JoinTable(
            name = "subscription_plan_hierarchy",
            joinColumns = @JoinColumn(name = "child_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_plan_id")
    )
    private List<SubscriptionPlan> parentPlans;

    @ManyToMany(mappedBy = "parentPlans")
    private List<SubscriptionPlan> childPlans;

    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanDuration> planDurations;

    @ManyToMany(mappedBy = "subscriptionPlans")
    private List<Movie> movies;


    @PrePersist
    protected void prePersist() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updateAt = Instant.now();
    }

}
