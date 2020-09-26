package boyko.babysitter.model;

public class Payment {

    private String message;
    private Double amount;

    public Payment(String message, Double amount) {
        this.message = message;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public Double getAmount() {
        return amount;
    }
}
