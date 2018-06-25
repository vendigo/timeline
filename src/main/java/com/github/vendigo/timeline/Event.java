package com.github.vendigo.timeline;

import java.time.LocalDate;

import com.github.vendigo.timeline.model.Rateplan;

import lombok.Value;

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

    public Event(LocalDate date, Rateplan rateplan, EventType eventType) {
        this.date = date;
        this.eventType = eventType;
        this.rateplan = rateplan;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %s}", date, eventType, rateplan == null ? "" : rateplan.getName());
    }
}
