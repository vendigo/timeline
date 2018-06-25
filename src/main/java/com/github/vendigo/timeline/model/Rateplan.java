package com.github.vendigo.timeline.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Rateplan {
    String name;
    LocalDate start;
    LocalDate end;
    BillingCycle billingCycle;
}
