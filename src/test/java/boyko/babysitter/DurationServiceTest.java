package boyko.babysitter;

import boyko.babysitter.service.DurationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationServiceTest {

    private static DurationService duration_service;

    @BeforeAll
    public static void setUp() {

        duration_service = new DurationService();
    }

    @Test
    void test_fractional_hours_duration_should_return_zero_when_start_after_end() {

        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(9, 59);

        assertEquals(0.0, duration_service.fractional_hours_duration(start, end));
    }

    @Test
    void test_fractional_hours_duration() {

        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(10, 15);
        assertEquals(0.25, duration_service.fractional_hours_duration(start, end));

        LocalTime start1 = LocalTime.of(10, 0);
        LocalTime end1 = LocalTime.of(17, 30);
        assertEquals(7.5, duration_service.fractional_hours_duration(start1, end1));

        LocalTime start2 = LocalTime.of(10, 0);
        LocalTime end2 = LocalTime.of(11, 54);
        assertEquals(1.9, duration_service.fractional_hours_duration(start2, end2));
    }

}