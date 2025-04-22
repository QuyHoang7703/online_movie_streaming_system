package com.example.OnlineMovieStreamingSystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanDuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private double price;
    private int durationInMonths;

    @ManyToOne
    @JoinColumn(name="subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;
}
