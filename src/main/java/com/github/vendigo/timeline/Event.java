package com.github.vendigo.timeline;

import com.github.vendigo.timeline.model.Rateplan;
import lombok.Value;

import java.time.LocalDate;

@Value
public class Event {
    LocalDate date;
    EventType eventType;
    Rateplan rateplan;

    public Event(LocalDate date, EventType eventType) {
        this.date = date;
        this.eventType = eventType;
        this.rateplan = null;
    }

    public Event(LocalDate date, Rateplan rateplan) {
        this.date = date;
        this.eventType = EventType.BILLING_CYCLE;
        this.rateplan = rateplan;
    }
}
