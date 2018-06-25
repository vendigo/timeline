package com.github.vendigo.timeline;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.github.vendigo.timeline.model.BilledPeriod;
import com.github.vendigo.timeline.model.BillingCycle;
import com.github.vendigo.timeline.model.BillingPeriod;
import com.github.vendigo.timeline.model.Rateplan;

public class BillingPeriodsBuilderTest {

    private static final LocalDate JAN_1 = LocalDate.of(2018, Month.JANUARY, 1);
    private static final LocalDate JAN_31 = LocalDate.of(2018, Month.JANUARY, 31);
    private static final LocalDate FEB_1 = LocalDate.of(2018, Month.FEBRUARY, 1);
    private static final LocalDate FEB_14 = LocalDate.of(2018, Month.FEBRUARY, 14);
    private static final LocalDate FEB_15 = LocalDate.of(2018, Month.FEBRUARY, 15);
    private static final LocalDate FEB_28 = LocalDate.of(2018, Month.FEBRUARY, 28);
    private static final LocalDate MAR_1 = LocalDate.of(2018, Month.MARCH, 1);
    private static final LocalDate MAR_16 = LocalDate.of(2018, Month.MARCH, 16);
    private static final LocalDate MAR_17 = LocalDate.of(2018, Month.MARCH, 17);
    private static final LocalDate MAR_31 = LocalDate.of(2018, Month.MARCH, 31);
    private static final LocalDate APR_1 = LocalDate.of(2018, Month.APRIL, 1);
    private static final LocalDate APR_30 = LocalDate.of(2018, Month.APRIL, 30);
    private static final LocalDate MAY_1 = LocalDate.of(2018, Month.MAY, 1);
    private static final LocalDate MAY_19 = LocalDate.of(2018, Month.MAY, 19);
    private static final LocalDate MAY_20 = LocalDate.of(2018, Month.MAY, 20);
    private static final LocalDate JUN_10 = LocalDate.of(2018, Month.JUNE, 10);
    private static final LocalDate JUN_11 = LocalDate.of(2018, Month.JUNE, 11);
    private static final LocalDate JUN_30 = LocalDate.of(2018, Month.JUNE, 30);
    private static final LocalDate JUL_1 = LocalDate.of(2018, Month.JULY, 1);
    private static final LocalDate JUL_31 = LocalDate.of(2018, Month.JULY, 31);
    private static final LocalDate AUG_1 = LocalDate.of(2018, Month.AUGUST, 1);
    private static final LocalDate FEB_28_19 = LocalDate.of(2019, Month.FEBRUARY, 28);
    private static final LocalDate MAY_1_19 = LocalDate.of(2019, Month.MAY, 1);
    private static final LocalDate DEC_31_20 = LocalDate.of(2020, Month.DECEMBER, 31);

    @Test
    public void withoutBilledPeriods() {
        final Rateplan rp1 = new Rateplan("rp1", JAN_1, FEB_14, BillingCycle.MONTHLY);
        final Rateplan rp2 = new Rateplan("rp2", FEB_15, MAR_31, BillingCycle.MONTHLY);
        final List<BillingPeriod> actualBillingPeriods = BillingPeriodsBuilder.buildBillingPeriods(Collections.emptyList(), Arrays.asList(rp1, rp2), MAR_16);
        final List<BillingPeriod> expectedBillingPeriods = Arrays.asList(
            new BillingPeriod(JAN_1, JAN_31, rp1),
            new BillingPeriod(FEB_1, FEB_14, rp1),
            new BillingPeriod(FEB_15, FEB_28, rp2)
        );
        assertEquals(expectedBillingPeriods, actualBillingPeriods);
    }

    @Test
    public void withOneBilledPeriod() {
        final Rateplan rp1 = new Rateplan("rp1", JAN_1, MAR_31, BillingCycle.QURTERLY);
        final List<BillingPeriod> actualBillingPeriods = BillingPeriodsBuilder.buildBillingPeriods(Collections.singletonList(new BilledPeriod(FEB_1, MAR_16)),
            Collections.singletonList(rp1), APR_1);
        final List<BillingPeriod> expectedBillingPeriods = Arrays.asList(
            new BillingPeriod(JAN_1, JAN_31, rp1),
            new BillingPeriod(MAR_17, MAR_31, rp1)
        );
        assertEquals(expectedBillingPeriods, actualBillingPeriods);
    }

    @Test
    public void twoBilledPeriods() {
        final Rateplan rp1 = new Rateplan("rp1", JAN_1, MAR_31, BillingCycle.QURTERLY);
        final Rateplan rp2 = new Rateplan("rp2", APR_1, JUL_31, BillingCycle.MONTHLY);
        final List<BillingPeriod> actualBillingPeriods = BillingPeriodsBuilder.buildBillingPeriods(
            Arrays.asList(new BilledPeriod(FEB_1, FEB_28), new BilledPeriod(MAY_20, JUN_10)),
            Arrays.asList(rp1, rp2), AUG_1);
        final List<BillingPeriod> expectedBillingPeriods = Arrays.asList(
            new BillingPeriod(JAN_1, JAN_31, rp1),
            new BillingPeriod(MAR_1, MAR_31, rp1),
            new BillingPeriod(APR_1, APR_30, rp2),
            new BillingPeriod(MAY_1, MAY_19, rp2),
            new BillingPeriod(JUN_11, JUN_30, rp2),
            new BillingPeriod(JUL_1, JUL_31, rp2)
        );
        assertEquals(expectedBillingPeriods, actualBillingPeriods);
    }

    @Test
    public void yearCycle() {
        final Rateplan rp1 = new Rateplan("rp1", JAN_1, FEB_28, BillingCycle.YEARLY);
        final Rateplan rp2 = new Rateplan("rp2", MAR_1, DEC_31_20, BillingCycle.YEARLY);
        final List<BillingPeriod> actualBillingPeriods = BillingPeriodsBuilder.buildBillingPeriods(
            Collections.emptyList(), Arrays.asList(rp1, rp2), MAY_1_19);
        final List<BillingPeriod> expectedBillingPeriods = Arrays.asList(
            new BillingPeriod(JAN_1, FEB_28, rp1),
            new BillingPeriod(MAR_1, FEB_28_19, rp2)
        );
        assertEquals(expectedBillingPeriods, actualBillingPeriods);
    }
}
