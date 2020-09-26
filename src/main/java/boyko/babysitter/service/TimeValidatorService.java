package boyko.babysitter.service;

import boyko.babysitter.model.TimeValidatorResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeValidatorService {

    public TimeValidatorResponse validate(LocalDateTime clock_in_datetime, LocalDateTime clock_out_datetime,
                                      LocalTime earliest_clock_in_time, LocalTime latest_clock_out_time) {

        LocalTime clock_in_time = clock_in_datetime.toLocalTime();
        if(clock_in_time.isAfter(latest_clock_out_time) && clock_in_time.isBefore(earliest_clock_in_time)) {

            return new TimeValidatorResponse(false, ErrorMessage.start_time_earlier_than_allowed);
        }

        LocalTime clock_out_time = clock_out_datetime.toLocalTime();
        if(clock_out_time.isAfter(latest_clock_out_time) && clock_out_time.isBefore(earliest_clock_in_time)) {

            return new TimeValidatorResponse(false, ErrorMessage.leave_time_later_than_allowed);
        }

        return new TimeValidatorResponse(true, null);
    }

    public class ErrorMessage {
        public static final String start_time_earlier_than_allowed = "Start time is earlier than the allowed.";
        public static final String leave_time_later_than_allowed = "Leave time is later than the allowed.";
    }
}
