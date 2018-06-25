package com.github.vendigo.timeline;

import static com.github.vendigo.timeline.DatePointsUtils.startsOfBillingCycles;
import static java.util.Comparator.comparing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.vendigo.timeline.model.BilledPeriod;
import com.github.vendigo.timeline.model.BillingPeriod;
import com.github.vendigo.timeline.model.Rateplan;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import one.util.streamex.StreamEx;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingPeriodsBuilder {

    private static final Comparator<Event> EVENT_COMPARATOR = comparing(Event::getDate)
        .thenComparing(event -> event.getEventType().ordinal());

    public static List<BillingPeriod> buildBillingPeriods(List<BilledPeriod> billedPeriods, List<Rateplan> rateplans,
        LocalDate billingDate) {
        List<Event> events = constructEvents(billedPeriods, rateplans, billingDate);
        System.out.println("Events: " + events);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Event prevEvent = events.get(0);
        boolean inBilledPeriod = prevEvent.getEventType() == EventType.START_OF_BILLED_PERIOD;
        List<BillingPeriod> billingPeriods = new ArrayList<>();

        for (int i = 1; i < events.size(); i++) {
            Event event = events.get(i);
            EventType eventType = event.getEventType();

            if (eventType == EventType.BILLING_DATE) {
                break;
            }

            if (eventType == EventType.END_OF_BILLED_PERIOD) {
                inBilledPeriod = false;
            }

            if (inBilledPeriod) {
                prevEvent = event;
                continue;
            }

            if (eventType == EventType.START_OF_BILLED_PERIOD) {
                inBilledPeriod = true;
            }

            createBillingPeriod(prevEvent, event).ifPresent(billingPeriods::add);
            prevEvent = event;
        }

        return billingPeriods;
    }

    private static Optional<BillingPeriod> createBillingPeriod(final Event prevEvent, final Event event) {
        final EventType prevType = prevEvent.getEventType();
        final EventType currentType = event.getEventType();

        if (currentType == EventType.END_OF_BILLED_PERIOD || (prevType == EventType.END_OF_RATE_PLAN && currentType == EventType.START_OF_RATE_PLAN)) {
            return Optional.empty();
        }

        LocalDate from = prevEvent.getDate();
        LocalDate to = event.getDate();
        Rateplan rateplan = firstNotNull(prevEvent.getRateplan(), event.getRateplan());

        if (prevType == EventType.END_OF_BILLED_PERIOD) {
            from = from.plusDays(1);
        }

        if (currentType != EventType.END_OF_RATE_PLAN) {
            to = to.minusDays(1);
        }

        return Optional.of(new BillingPeriod(from, to, rateplan));
    }

    private static Rateplan firstNotNull(final Rateplan first, final Rateplan second) {
        return first == null ? second : first;
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
            new Event(rateplan.getStart(), rateplan, EventType.START_OF_RATE_PLAN),
            new Event(rateplan.getEnd(), rateplan, EventType.END_OF_RATE_PLAN))
            .append(startsOfBillingCycles(rateplan).map(date -> new Event(date, rateplan, EventType.BILLING_CYCLE)));
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
