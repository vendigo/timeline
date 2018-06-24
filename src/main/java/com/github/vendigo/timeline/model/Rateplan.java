package com.github.vendigo.timeline.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Rateplan {
    LocalDate start;
    LocalDate end;
    BillingCycle billingCycle;
}
