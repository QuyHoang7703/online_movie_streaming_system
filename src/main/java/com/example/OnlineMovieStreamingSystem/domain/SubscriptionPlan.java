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
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private Instant createAt;
    private Instant updateAt;

    @OneToMany(mappedBy = "parentSubscriptionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionPlan> childSubscriptionPlans;

    @ManyToOne
    @JoinColumn(name="parent_subscription_plan_id")
    private SubscriptionPlan parentSubscriptionPlan;

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
