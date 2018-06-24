package com.github.vendigo.timeline.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class BilledPeriod {
    LocalDate start;
    LocalDate end;
}
