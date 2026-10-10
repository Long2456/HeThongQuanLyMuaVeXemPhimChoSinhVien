package cinema.model;

public class Customer extends User {
    public enum CustomerType {
        REGULAR("Khách thường", 0.0),
        STUDENT("Sinh viên", 0.15),
        VIP("Thành viên VIP", 0.10);

        private final String label;
        private final double discountRate;

        CustomerType(String label, double discountRate) {
            this.label = label;
            this.discountRate = discountRate;
        }

        public double getDiscountRate() {
            return discountRate;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final String phone;
    private final String email;
    private final CustomerType type;

    public Customer(String id, String fullName, String phone, String email, CustomerType type) {
        super(id, fullName);
        this.phone = phone;
        this.email = email;
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public CustomerType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getFullName() + " - " + phone;
    }
}