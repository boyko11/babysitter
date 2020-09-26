package boyko.babysitter.service;

import boyko.babysitter.model.Payment;

import java.time.LocalDateTime;

public interface PayCalculatorService {

    Payment calculate_pay(LocalDateTime clock_in_time, LocalDateTime clock_out_time);
}
