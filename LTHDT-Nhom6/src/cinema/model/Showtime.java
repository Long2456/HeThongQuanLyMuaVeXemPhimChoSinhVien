package cinema.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Showtime {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String id;
    private final Movie movie;
    private final String roomName;
    private final LocalDateTime startTime;
    private final long basePrice;
    private final List<Seat> seats;

    public Showtime(String id, Movie movie, String roomName, LocalDateTime startTime, long basePrice) {
        this.id = id;
        this.movie = movie;
        this.roomName = roomName;
        this.startTime = startTime;
        this.basePrice = basePrice;
        this.seats = createSeats();
    }

    private List<Seat> createSeats() {
        List<Seat> result = new ArrayList<>();
        for (int number = 1; number <= 8; number++) {
            Seat.SeatType type = number == 8 ? Seat.SeatType.COUPLE
                    : number >= 6 ? Seat.SeatType.VIP : Seat.SeatType.STANDARD;
            result.add(new Seat("A" + number, type));
        }
        for (int number = 1; number <= 8; number++) {
            Seat.SeatType type = number >= 6 ? Seat.SeatType.VIP : Seat.SeatType.STANDARD;
            result.add(new Seat("B" + number, type));
        }
        return Collections.unmodifiableList(result);
    }

    public String getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public String getRoomName() {
        return roomName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getBasePrice() {
        return basePrice;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    @Override
    public String toString() {
        return movie.getTitle() + " | " + startTime.format(DISPLAY_FORMAT) + " | " + roomName;
    }
}