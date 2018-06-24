package com.github.vendigo.timeline;

import com.github.vendigo.timeline.model.BilledPeriod;
import com.github.vendigo.timeline.model.BillingPeriod;
import com.github.vendigo.timeline.model.Rateplan;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import one.util.streamex.StreamEx;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.github.vendigo.timeline.DatePointsUtils.startsOfBillingCycles;
import static java.util.Comparator.comparing;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingPeriodsBuilder {

    private static final Comparator<Event> EVENT_COMPARATOR = comparing(Event::getDate)
            .thenComparing(event -> event.getEventType().ordinal());

    public static List<BillingPeriod> buildBillingPeriods(List<BilledPeriod> billedPeriods, List<Rateplan> rateplans,
                                                          LocalDate billingDate) {
        List<Event> events = constructEvents(billedPeriods, rateplans, billingDate);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Event prevEvent = events.get(0);
        boolean inBilledPeriod = updateInBilledPeriod(prevEvent.getEventType(), false);
        List<BillingPeriod> billingPeriods = new ArrayList<>();

        for (int i = 1; i < events.size(); i++) {
            Event event = events.get(i);
            EventType eventType = event.getEventType();

            if (eventType == EventType.BILLING_DATE) {
                break;
            }

            inBilledPeriod = updateInBilledPeriod(eventType, inBilledPeriod);
            if (inBilledPeriod) {
                prevEvent = event;
                continue;
            }

            billingPeriods.add(new BillingPeriod(prevEvent.getDate(), event.getDate(), prevEvent.getRateplan()));
        }

        return billingPeriods;
    }

    private static boolean updateInBilledPeriod(EventType eventType, boolean inBilledPeriod) {
        switch (eventType) {
            case START_OF_BILLED_PERIOD:
                return true;
            case END_OF_BILLED_PERIOD:
                return false;
            default:
                return inBilledPeriod;
        }
    }


    private static List<Event> constructEvents(List<BilledPeriod> billedPeriods, List<Rateplan> rateplans,
                                               LocalDate billingDate) {
        return StreamEx
                .of(billedPeriodsEvents(billedPeriods))
                .append(ratePlansEvents(rateplans))
                .append(billingDateEvent(billingDate))
                .sorted(EVENT_COMPARATOR)
                .toList();
    }

    private static Stream<Event> billingDateEvent(LocalDate billingDate) {
        return Stream.of(new Event(billingDate, EventType.BILLING_DATE));
    }

    private static Stream<Event> ratePlansEvents(List<Rateplan> rateplans) {
        return rateplans.stream().flatMap(BillingPeriodsBuilder::ratePlanEvents);
    }

    private static Stream<Event> ratePlanEvents(Rateplan rateplan) {
        return StreamEx.of(
                new Event(rateplan.getStart(), rateplan),
                new Event(rateplan.getEnd(), rateplan))
                .append(startsOfBillingCycles(rateplan).map(date -> new Event(date, rateplan)));
    }

    private static Stream<Event> billedPeriodsEvents(List<BilledPeriod> billedPeriods) {
        return billedPeriods.stream().flatMap(BillingPeriodsBuilder::billedPeriodEvents);
    }

    private static Stream<Event> billedPeriodEvents(BilledPeriod billedPeriod) {
        return Stream.of(
                new Event(billedPeriod.getStart(), EventType.START_OF_BILLED_PERIOD),
                new Event(billedPeriod.getEnd(), EventType.END_OF_BILLED_PERIOD));
    }
}
