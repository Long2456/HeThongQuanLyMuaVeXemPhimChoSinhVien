package cinema.service;

import cinema.model.Customer;
import cinema.model.Movie;
import cinema.model.Seat;
import cinema.model.Showtime;
import cinema.model.Ticket;
import cinema.repository.CinemaRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CinemaService {
    private final CinemaRepository repository;
    private final List<Customer> customers;
    private final List<Ticket> tickets;
    private final List<Movie> movies;
    private final List<Showtime> showtimes;

    public CinemaService(CinemaRepository repository) throws IOException {
        this.repository = repository;
        this.customers = new ArrayList<>(repository.loadCustomers());
        this.tickets = new ArrayList<>(repository.loadTickets());
        this.movies = List.of(
                new Movie("M001", "Hành Trình Sao Băng", "Phiêu lưu", 125, 13, true),
                new Movie("M002", "Thành Phố Trong Sương", "Tâm lý", 110, 16, true),
                new Movie("M003", "Ngày Cuối Tuần", "Hài", 98, 0, true));
        LocalDateTime firstShow = LocalDateTime.now().plusHours(2).withMinute(0).withSecond(0).withNano(0);
        this.showtimes = List.of(
                new Showtime("S001", movies.get(0), "Phong 1", firstShow, 90000),
                new Showtime("S002", movies.get(1), "Phong 2", firstShow.plusHours(2), 85000),
                new Showtime("S003", movies.get(2), "Phong 1", firstShow.plusDays(1), 80000));
    }

    public List<Customer> getCustomers() {
        return List.copyOf(customers);
    }

    public List<Ticket> getTickets() {
        return List.copyOf(tickets);
    }

    public List<Movie> getMovies() {
        return List.copyOf(movies);
    }

    public List<Showtime> getShowtimes() {
        return List.copyOf(showtimes);
    }

    public Customer addCustomer(String name, String phone, String email, Customer.CustomerType type)
            throws IOException {
        Customer customer = createValidatedCustomer(newId("KH"), name, phone, email, type, null);
        customers.add(customer);
        repository.saveCustomers(customers);
        return customer;
    }

    public Customer updateCustomer(String id, String name, String phone, String email, Customer.CustomerType type)
            throws IOException {
        int customerIndex = findCustomerIndex(id);
        if (customerIndex < 0) {
            throw new IllegalArgumentException("Khach hang khong ton tai.");
        }
        Customer updatedCustomer = createValidatedCustomer(id, name, phone, email, type, id);
        Customer previousCustomer = customers.get(customerIndex);
        customers.set(customerIndex, updatedCustomer);
        try {
            repository.saveCustomers(customers);
        } catch (IOException ex) {
            customers.set(customerIndex, previousCustomer);
            throw ex;
        }
        return updatedCustomer;
    }

    public void deleteCustomer(String id) throws IOException {
        int customerIndex = findCustomerIndex(id);
        if (customerIndex < 0) {
            throw new IllegalArgumentException("Khach hang khong ton tai.");
        }
        Customer deletedCustomer = customers.remove(customerIndex);
        try {
            repository.saveCustomers(customers);
        } catch (IOException ex) {
            customers.add(customerIndex, deletedCustomer);
            throw ex;
        }
    }

    private Customer createValidatedCustomer(String id, String name, String phone, String email,
            Customer.CustomerType type, String excludedCustomerId) {
        String cleanName = name == null ? "" : name.trim();
        String cleanPhone = phone == null ? "" : phone.trim();
        String cleanEmail = email == null ? "" : email.trim();
        if (cleanName.isEmpty() || cleanPhone.isEmpty() || cleanEmail.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap day du ho ten, so dien thoai va email.");
        }
        if (!cleanPhone.matches("[0-9+(). -]{8,20}")) {
            throw new IllegalArgumentException("So dien thoai khong hop le.");
        }
        if (!cleanEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Email khong hop le.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Loai khach hang khong hop le.");
        }
        if (customers.stream().anyMatch(customer -> customer.getPhone().equals(cleanPhone)
                && !customer.getId().equals(excludedCustomerId))) {
            throw new IllegalArgumentException("So dien thoai nay da duoc dang ky.");
        }
        return new Customer(id, cleanName, cleanPhone, cleanEmail, type);
    }

    private int findCustomerIndex(String id) {
        for (int index = 0; index < customers.size(); index++) {
            if (customers.get(index).getId().equals(id)) {
                return index;
            }
        }
        return -1;
    }

    public List<Seat> getAvailableSeats(Showtime showtime) {
        return showtime.getSeats().stream()
                .filter(seat -> tickets.stream().noneMatch(ticket ->
                        ticket.getShowtimeId().equals(showtime.getId())
                                && ticket.getSeatId().equals(seat.getId())))
                .toList();
    }

    public long calculatePrice(Customer customer, Showtime showtime, Seat seat) {
        long priceBeforeDiscount = showtime.getBasePrice() + seat.getType().getSurcharge();
        return Math.round(priceBeforeDiscount * (1 - customer.getType().getDiscountRate()));
    }

    public Ticket bookTicket(Customer customer, Showtime showtime, Seat seat) throws IOException {
        if (!customers.contains(customer)) {
            throw new IllegalArgumentException("Khach hang khong ton tai.");
        }
        if (!showtimes.contains(showtime) || !showtime.getMovie().isShowing()) {
            throw new IllegalArgumentException("Suat chieu khong ton tai hoac phim da ngung chieu.");
        }
        if (showtime.getSeats().stream().noneMatch(existing -> existing.getId().equals(seat.getId()))) {
            throw new IllegalArgumentException("Ghe khong ton tai trong phong chieu.");
        }
        if (getAvailableSeats(showtime).stream().noneMatch(available -> available.getId().equals(seat.getId()))) {
            throw new IllegalArgumentException("Ghe nay da duoc dat.");
        }
        Ticket ticket = new Ticket(newId("VE"), customer.getId(), showtime.getId(), seat.getId(),
                calculatePrice(customer, showtime, seat), false, null);
        tickets.add(ticket);
        repository.saveTickets(tickets);
        return ticket;
    }

    public void payTicket(String ticketId, Ticket.PaymentMethod method) throws IOException {
        int ticketIndex = -1;
        for (int index = 0; index < tickets.size(); index++) {
            if (tickets.get(index).getId().equals(ticketId)) {
                ticketIndex = index;
                break;
            }
        }
        if (ticketIndex < 0) {
            throw new IllegalArgumentException("Ve khong ton tai.");
        }
        Ticket ticket = tickets.get(ticketIndex);
        if (ticket.isPaid()) {
            throw new IllegalArgumentException("Ve nay da thanh toan.");
        }
        tickets.set(ticketIndex, ticket.markPaid(method));
        repository.saveTickets(tickets);
    }

    public Customer findCustomer(String id) {
        return customers.stream().filter(customer -> customer.getId().equals(id)).findFirst().orElse(null);
    }

    public Showtime findShowtime(String id) {
        return showtimes.stream().filter(showtime -> showtime.getId().equals(id)).findFirst().orElse(null);
    }

    private String newId(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}