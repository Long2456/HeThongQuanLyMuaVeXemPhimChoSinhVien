package cinema.model;

public class Seat {
    public enum SeatType {
        STANDARD("Thường", 0),
        COUPLE("Đôi", 30000),
        VIP("VIP", 50000);

        private final String label;
        private final long surcharge;

        SeatType(String label, long surcharge) {
            this.label = label;
            this.surcharge = surcharge;
        }

        public long getSurcharge() {
            return surcharge;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private final String id;
    private final SeatType type;

    public Seat(String id, SeatType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public SeatType getType() {
        return type;
    }

    @Override
    public String toString() {
        return id + " - " + type;
    }
}