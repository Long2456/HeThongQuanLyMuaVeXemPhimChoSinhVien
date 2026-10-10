package cinema.repository;

import cinema.model.Customer;
import cinema.model.Ticket;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class CinemaRepository {
    private final Path customersFile;
    private final Path ticketsFile;

    public CinemaRepository(Path dataDirectory) throws IOException {
        this.customersFile = dataDirectory.resolve("customers.csv");
        this.ticketsFile = dataDirectory.resolve("tickets.csv");
        Files.createDirectories(dataDirectory);
        createIfMissing(customersFile);
        createIfMissing(ticketsFile);
    }

    private void createIfMissing(Path file) throws IOException {
        if (Files.notExists(file)) {
            Files.createFile(file);
        }
    }

    public List<Customer> loadCustomers() throws IOException {
        List<Customer> customers = new ArrayList<>();
        for (String line : Files.readAllLines(customersFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            String[] fields = unpack(line);
            customers.add(new Customer(fields[0], fields[1], fields[2], fields[3],
                    Customer.CustomerType.valueOf(fields[4])));
        }
        return customers;
    }

    public List<Ticket> loadTickets() throws IOException {
        List<Ticket> tickets = new ArrayList<>();
        for (String line : Files.readAllLines(ticketsFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            String[] fields = unpack(line);
            Ticket.PaymentMethod method = fields[6].isEmpty()
                    ? null : Ticket.PaymentMethod.valueOf(fields[6]);
            tickets.add(new Ticket(fields[0], fields[1], fields[2], fields[3],
                    Long.parseLong(fields[4]), Boolean.parseBoolean(fields[5]), method));
        }
        return tickets;
    }

    public void saveCustomers(Collection<Customer> customers) throws IOException {
        List<String> lines = customers.stream()
                .map(customer -> pack(customer.getId(), customer.getFullName(), customer.getPhone(),
                        customer.getEmail(), customer.getType().name()))
                .toList();
        Files.write(customersFile, lines, StandardCharsets.UTF_8);
    }

    public void saveTickets(Collection<Ticket> tickets) throws IOException {
        List<String> lines = tickets.stream()
                .map(ticket -> pack(ticket.getId(), ticket.getCustomerId(), ticket.getShowtimeId(),
                        ticket.getSeatId(), Long.toString(ticket.getFinalPrice()),
                        Boolean.toString(ticket.isPaid()), ticket.getPaymentMethod() == null
                                ? "" : ticket.getPaymentMethod().name()))
                .toList();
        Files.write(ticketsFile, lines, StandardCharsets.UTF_8);
    }

    private String pack(String... fields) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return String.join(",", java.util.Arrays.stream(fields)
                .map(value -> encoder.encodeToString(value.getBytes(StandardCharsets.UTF_8)))
                .toList());
    }

    private String[] unpack(String line) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return java.util.Arrays.stream(line.split(",", -1))
                .map(value -> new String(decoder.decode(value), StandardCharsets.UTF_8))
                .toArray(String[]::new);
    }
}