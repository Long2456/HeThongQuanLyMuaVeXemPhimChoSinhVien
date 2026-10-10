package cinema.model;

public class Ticket {
    public enum PaymentMethod {
        CASH("Tiền mặt"), BANK_TRANSFER("Chuyển khoản"), EWALLET("Ví điện tử");

        private final String label;

        PaymentMethod(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final String id;
    private final String customerId;
    private final String showtimeId;
    private final String seatId;
    private final long finalPrice;
    private final boolean paid;
    private final PaymentMethod paymentMethod;

    public Ticket(String id, String customerId, String showtimeId, String seatId, long finalPrice,
                  boolean paid, PaymentMethod paymentMethod) {
        this.id = id;
        this.customerId = customerId;
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.finalPrice = finalPrice;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public String getSeatId() {
        return seatId;
    }

    public long getFinalPrice() {
        return finalPrice;
    }

    public boolean isPaid() {
        return paid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Ticket markPaid(PaymentMethod method) {
        return new Ticket(id, customerId, showtimeId, seatId, finalPrice, true, method);
    }
}