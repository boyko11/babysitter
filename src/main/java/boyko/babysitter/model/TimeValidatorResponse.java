package boyko.babysitter.model;

public class TimeValidatorResponse {

    private Boolean valid_times;
    private String error_message;

    public TimeValidatorResponse(Boolean valid_times, String error_message) {
        this.valid_times = valid_times;
        this.error_message = error_message;
    }

    public Boolean getValid_times() {
        return valid_times;
    }

    public String getError_message() {
        return error_message;
    }
}
