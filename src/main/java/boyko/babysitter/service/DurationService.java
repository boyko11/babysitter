package boyko.babysitter.service;

import java.time.Duration;
import java.time.LocalTime;

public class DurationService {

    public Double fractional_hours_duration(LocalTime start, LocalTime end) {

        long clock_in_to_bedtime_minutes = Duration.between(start, end).toMinutes();
        clock_in_to_bedtime_minutes = Math.max(0, clock_in_to_bedtime_minutes);
        long hours = clock_in_to_bedtime_minutes / 60;
        long minutes = clock_in_to_bedtime_minutes % 60;

        return (double) hours + (double) minutes / 60.0;
    }
}
