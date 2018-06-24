package com.github.vendigo.timeline;

import com.github.vendigo.timeline.model.BillingCycle;
import com.github.vendigo.timeline.model.Rateplan;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.threeten.extra.YearQuarter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatePointsUtils {

    static Stream<LocalDate> startsOfBillingCycles(Rateplan rateplan) {
        LocalDate start = rateplan.getStart();
        LocalDate end = rateplan.getEnd();
        BillingCycle billingCycle = rateplan.getBillingCycle();

        switch (billingCycle) {
            case MONTHLY:
                return months(start, end);
            case QURTERLY:
                quarters(start, end);
            case YEARLY:
                return years(start, end);
            default:
                throw new IllegalArgumentException("Unsupported billingCycle: " + billingCycle);
        }
    }

    private static Stream<LocalDate> months(LocalDate from, LocalDate to) {
        LocalDate date = startOfNextMonth(from);
        List<LocalDate> dates = new ArrayList<>();

        while (date.isBefore(to)) {
            dates.add(date);
            date.plusMonths(1);
        }
        return dates.stream();
    }

    private static Stream<LocalDate> quarters(LocalDate from, LocalDate to) {
        YearQuarter quarter = YearQuarter.from(startOfNextMonth(from));
        LocalDate date = quarter.plusQuarters(1).atDay(1);

        List<LocalDate> dates = new ArrayList<>();

        while (date.isBefore(to)) {
            dates.add(date);
            date.plusMonths(3);
        }
        return dates.stream();
    }

    private static Stream<LocalDate> years(LocalDate from, LocalDate to) {
        LocalDate date = from.plusYears(1);

        List<LocalDate> dates = new ArrayList<>();
        while (date.isBefore(to)) {
            dates.add(date);
            date.plusYears(1);
        }
        return dates.stream();
    }

    private static LocalDate startOfNextMonth(LocalDate from) {
        return from.withDayOfMonth(1).plusMonths(1);
    }
}
