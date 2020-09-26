package boyko.babysitter;

import boyko.babysitter.model.TimeValidatorResponse;
import boyko.babysitter.service.TimeValidatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TimeValidatorTest {

    private static TimeValidatorService timeValidatorService;
    private static LocalTime earliest_clock_in_time;
    private static LocalTime latest_clock_out_time;

    @BeforeAll
    public static void setUp() {

        earliest_clock_in_time = LocalTime.of(17, 0, 0);
        latest_clock_out_time = LocalTime.of(4, 0, 0);

        timeValidatorService = new TimeValidatorService();
    }

    @Test
    void test_validate_should_return_error_when_start_earlier_than_allowed_in_the_pm() {

        LocalDateTime earlier_than_allowed_time = LocalDateTime.of(LocalDate.now(),
                earliest_clock_in_time.minusSeconds(1));

        assert_start_time_earlier_error_message_and_zero_amount(earlier_than_allowed_time);
    }

    @Test
    void test_validate_should_return_error_when_start_earlier_than_allowed_in_the_am() {

        LocalDateTime earlier_than_allowed_time = LocalDateTime.of(LocalDate.now(),
                latest_clock_out_time.plusSeconds(1));

        assert_start_time_earlier_error_message_and_zero_amount(earlier_than_allowed_time);
    }

    private void assert_start_time_earlier_error_message_and_zero_amount(LocalDateTime earlier_than_allowed_time) {

        TimeValidatorResponse response = timeValidatorService.validate(earlier_than_allowed_time,
                earlier_than_allowed_time.plusHours(8), earliest_clock_in_time, latest_clock_out_time);

        assertFalse(response.getValid_times());
        assertEquals(TimeValidatorService.ErrorMessage.start_time_earlier_than_allowed, response.getError_message());
    }


    @Test
    void test_validate_should_return_error_when_leave_later_than_allowed_in_the_am() {

        LocalDateTime later_than_allowed_time = LocalDateTime.of(LocalDate.now(),
                latest_clock_out_time.plusSeconds(1));

        assert_leave_time_later_error_message_and_zero_amount(later_than_allowed_time);
    }

    @Test
    void test_validate_should_return_error_when_leave_later_than_allowed_in_the_pm() {

        LocalDateTime later_than_allowed_time = LocalDateTime.of(LocalDate.now(),
                earliest_clock_in_time.minusSeconds(1));

        assert_leave_time_later_error_message_and_zero_amount(later_than_allowed_time);
    }

    private void assert_leave_time_later_error_message_and_zero_amount(LocalDateTime later_than_allowed_time) {

        TimeValidatorResponse response = timeValidatorService.validate(LocalDateTime.of(LocalDate.now(),
                earliest_clock_in_time), later_than_allowed_time, earliest_clock_in_time, latest_clock_out_time);

        assertFalse(response.getValid_times());
        assertEquals(TimeValidatorService.ErrorMessage.leave_time_later_than_allowed, response.getError_message());
    }

}