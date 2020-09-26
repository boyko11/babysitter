package boyko.babysitter.service;

import boyko.babysitter.model.Payment;
import boyko.babysitter.model.TimeValidatorResponse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PayCalculatorServiceOneNightImpl implements PayCalculatorService {

    private TimeValidatorService time_validator_service;
    private DurationService duration_service;
    private LocalTime earliest_clock_in_time;
    private LocalTime latest_clock_out_time;
    private LocalTime bed_time;
    private final LocalTime midnight = LocalTime.of(0, 0);

    //shift everything to be within the same date for more readable calculations
    private Long minutes_diff_from_midnight;
    private LocalTime first_period_end;
    private LocalTime second_period_end;
    private LocalTime third_period_end;

    private Double start_to_bedtime_hourly_rate;
    private Double bedtime_midnight_hourly_rate;
    private Double midnight_to_leave_hourly_rate;


    public PayCalculatorServiceOneNightImpl(TimeValidatorService time_validator_service,
                                            DurationService duration_service,
                                            LocalTime earliest_clock_in_time, LocalTime latest_clock_out_time,
                                            LocalTime bed_time, Double start_to_bedtime_hourly_rate,
                                            Double bedtime_midnight_hourly_rate, Double midnight_to_leave_hourly_rate) {

        this.time_validator_service = time_validator_service;
        this.duration_service = duration_service;
        this.earliest_clock_in_time = earliest_clock_in_time;
        this.latest_clock_out_time = latest_clock_out_time;
        this.bed_time = bed_time;

        minutes_diff_from_midnight =
                Duration.between(LocalTime.of(0, 0), earliest_clock_in_time).toMinutes();

        this.first_period_end = this.bed_time.minusMinutes(minutes_diff_from_midnight);
        this.second_period_end = this.midnight.minusMinutes(minutes_diff_from_midnight);
        this.third_period_end = this.latest_clock_out_time.minusMinutes(minutes_diff_from_midnight);


        this.start_to_bedtime_hourly_rate = start_to_bedtime_hourly_rate;
        this.bedtime_midnight_hourly_rate = bedtime_midnight_hourly_rate;
        this.midnight_to_leave_hourly_rate = midnight_to_leave_hourly_rate;
    }

    @Override
    public Payment calculate_pay(LocalDateTime clock_in_datetime, LocalDateTime clock_out_datetime) {

        TimeValidatorResponse timeValidatorResponse = this.time_validator_service.validate(clock_in_datetime,
                clock_out_datetime, earliest_clock_in_time, latest_clock_out_time);

        if(!timeValidatorResponse.getValid_times()) {
            return new Payment(timeValidatorResponse.getError_message(), 0.0);
        }

        //shift everything to be within the same date for more readable calculations
        LocalDateTime clock_in_datetime_to_use = clock_in_datetime.minusMinutes(this.minutes_diff_from_midnight);
        LocalDateTime clock_out_datetime_to_use = clock_out_datetime.minusMinutes(this.minutes_diff_from_midnight);

        LocalTime clock_out_time = clock_out_datetime_to_use.toLocalTime();
        LocalTime end_time_first_period = clock_out_time.isBefore(this.first_period_end) ?
                clock_out_time : this.first_period_end;
        Double start_to_bedtime_hours = this.duration_service.fractional_hours_duration(
                clock_in_datetime_to_use.toLocalTime(), end_time_first_period);

        LocalTime end_time_second_period = clock_out_time.isBefore(this.second_period_end) ?
                clock_out_time : this.second_period_end;
        Double bedtime_to_midnight_hours = this.duration_service.fractional_hours_duration(first_period_end,
                end_time_second_period);

        LocalTime end_time_third_period = clock_out_time.isBefore(this.third_period_end) ?
                clock_out_time : this.third_period_end;
        Double midnight_to_leave_hours = this.duration_service.fractional_hours_duration(second_period_end,
                end_time_third_period);

        double pay_amount =  Math.round(start_to_bedtime_hours) * this.start_to_bedtime_hourly_rate +
                Math.round(bedtime_to_midnight_hours) * this.bedtime_midnight_hourly_rate +
                Math.round(midnight_to_leave_hours) * this.midnight_to_leave_hourly_rate;

        return new Payment(null, pay_amount);
    }
}
