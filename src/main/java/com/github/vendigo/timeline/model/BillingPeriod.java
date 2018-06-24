package com.github.vendigo.timeline.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BillingPeriod {
    LocalDate start;
    LocalDate end;
    Rateplan rateplan;
}
