package com.github.vendigo.timeline.model;

import java.time.LocalDate;

import lombok.Value;

@Value
public class BillingPeriod {

    LocalDate start;
    LocalDate end;
    Rateplan rateplan;

    @Override
    public String toString() {
        return String.format("[%s - %s] %s", start, end, rateplan == null ? "" : rateplan.getName());
    }
}
