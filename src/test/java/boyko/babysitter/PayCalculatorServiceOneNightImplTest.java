package boyko.babysitter;

import boyko.babysitter.model.Payment;
import boyko.babysitter.service.DurationService;
import boyko.babysitter.service.PayCalculatorService;
import boyko.babysitter.service.PayCalculatorServiceOneNightImpl;
import boyko.babysitter.service.TimeValidatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PayCalculatorServiceOneNightImplTest {

    private static TimeValidatorService time_validator_service;
    private static DurationService duration_service;
    private static PayCalculatorService payCalculatorService;
    private static LocalTime earliest_clock_in_time;
    private static LocalTime latest_clock_out_time;
    private static LocalTime bed_time;
    private static Double start_to_bedtime_hourly_rate;
    private static Double bedtime_midnight_hourly_rate;
    private static Double midnight_to_leave_hourly_rate;

    @BeforeAll
    public static void setUp() {

        time_validator_service = new TimeValidatorService();
        duration_service = new DurationService();
        earliest_clock_in_time = LocalTime.of(17, 0, 0);
        latest_clock_out_time = LocalTime.of(4, 0, 0);
        bed_time = LocalTime.of(22, 0, 0);
        start_to_bedtime_hourly_rate = 12.0;
        bedtime_midnight_hourly_rate = 8.0;
        midnight_to_leave_hourly_rate = 16.0;

        payCalculatorService = new PayCalculatorServiceOneNightImpl(time_validator_service, duration_service,
                earliest_clock_in_time, latest_clock_out_time, bed_time, start_to_bedtime_hourly_rate,
                bedtime_midnight_hourly_rate, midnight_to_leave_hourly_rate);
    }

    @Test
    void test_calculate_pay_should_return_error_when_invalid_times() {

        LocalDateTime earlier_than_allowed_time = LocalDateTime.of(LocalDate.now(),
                earliest_clock_in_time.minusSeconds(1));

        Payment payment = payCalculatorService.calculate_pay(earlier_than_allowed_time,
                earlier_than_allowed_time.plusHours(8));

        assertEquals(TimeValidatorService.ErrorMessage.start_time_earlier_than_allowed, payment.getMessage());
        assertEquals(0.0, payment.getAmount());
    }

    @Test
    void test_calculate_pay_clockout_before_bedtime_last_hour_under_30_minutes() {


        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);

        Payment payment = payCalculatorService.calculate_pay(start, start.plusMinutes(61));

        assertNull(payment.getMessage());
        assertEquals(12.0, payment.getAmount());
    }

    @Test
    void test_calculate_pay_clockout_before_bedtime_last_hour_over_30_minutes() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);
        Payment payment = payCalculatorService.calculate_pay(start, start.plusMinutes(91));

        assertNull(payment.getMessage());
        assertEquals(24.0, payment.getAmount());

    }

    @Test
    void test_calculate_pay_clockout_AT_bedtime() {


        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);

        Payment payment = payCalculatorService.calculate_pay(start, start.plusHours(5));

        assertNull(payment.getMessage());
        assertEquals(60.0, payment.getAmount());
    }

    @Test
    void test_calculate_pay_clockout_before_midnight_last_hour_under_30_minutes() {


        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);

        Payment payment = payCalculatorService.calculate_pay(start, start.plusMinutes(361));

        assertNull(payment.getMessage());
        //5 to 10 - 5hrs* 12 = 60 +
        //10 to 11:01 1hr*8 = 8
        assertEquals(68.0, payment.getAmount());
    }

    @Test
    void test_calculate_pay_clockout_before_midnight_last_hour_over_30_minutes() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);
        Payment payment = payCalculatorService.calculate_pay(start, start.plusMinutes(391));

        assertNull(payment.getMessage());
        //5 to 10 -   5hrs * 12 = 60 +
        //10 to 11:31 2hr *   8 = 16
        assertEquals(76.0, payment.getAmount());

    }

    @Test
    void test_calculate_pay_clockout_AT_midnight() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);
        Payment payment = payCalculatorService.calculate_pay(start, start.plusHours(7));

        assertNull(payment.getMessage());
        //5 to 10 -   5hrs * 12 = 60 +
        //10 to 00:00 2hr *   8 = 16
        assertEquals(76.0, payment.getAmount());
    }


    void test_calculate_pay_clockout_before_latest_last_hour_under_30_minutes() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), latest_clock_out_time);
        Payment payment = payCalculatorService.calculate_pay(start, end.minusMinutes(31));

        assertNull(payment.getMessage());
        //5 to 10 -   5hrs * 12 = 60 +
        //10 to 00:00 2hr *   8 = 16
        //0 to 4      3hr *  16 = 48
        assertEquals(124.0, payment.getAmount());
    }

    @Test
    void test_calculate_pay_clockout_before_latest_last_hour_over_30_minutes() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), latest_clock_out_time);
        Payment payment = payCalculatorService.calculate_pay(start, end.minusMinutes(29));

        assertNull(payment.getMessage());
        //5 to 10 -   5hrs * 12 = 60 +
        //10 to 00:00 2hr *   8 = 16
        //0 to 4      4hr *  16 = 48
        assertEquals(140.0, payment.getAmount());
    }

    @Test
    void test_calculate_pay_clockout_AT_latest_clock_out_time() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), latest_clock_out_time);
        Payment payment = payCalculatorService.calculate_pay(start, end);

        assertNull(payment.getMessage());
        //5 to 10 -   5hrs * 12 = 60 +
        //10 to 00:00 2hr *   8 = 16
        //0 to 4      4hr *  16 = 64
        assertEquals(140, payment.getAmount());
    }

    @Test
    void test_calculate_pay_start_after_earliest_time_before_bedtime_first_hour_over30() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time.plusMinutes(61));
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), latest_clock_out_time);
        Payment payment = payCalculatorService.calculate_pay(start, end);

        assertNull(payment.getMessage());
        //6:01 to 10 -   4hrs * 12 = 48 +
        //10 to 00:00    2hr *   8 = 16
        //0 to 4         4hr *  16 = 64
        assertEquals(128, payment.getAmount());
    }

    @Test
    void test_calculate_pay_start_after_earliest_time_before_bedtime_first_hour_under30() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time.plusMinutes(91));
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), latest_clock_out_time);
        Payment payment = payCalculatorService.calculate_pay(start, end);

        assertNull(payment.getMessage());
        //6:31 to 10 -   3hrs * 12 = 36 +
        //10 to 00:00    2hr *   8 = 16
        //0 to 4         4hr *  16 = 64
        assertEquals(116, payment.getAmount());
    }

    @Test
    void test_calculate_pay_start_after_earliest_time_before_end_before_latest_time() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), earliest_clock_in_time.plusMinutes(91));
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(1), latest_clock_out_time).minusMinutes(91);
        Payment payment = payCalculatorService.calculate_pay(start, end);

        assertNull(payment.getMessage());
        //6:31 to 10 -   3hrs * 12 = 36 +
        //10 to 00:00    2hr *   8 = 16
        //0 to 2:29      2hr *  16 = 32
        assertEquals(84, payment.getAmount());
    }
}