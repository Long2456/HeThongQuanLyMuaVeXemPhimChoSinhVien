import cinema.model.Customer;
import cinema.repository.CinemaRepository;
import cinema.service.CinemaService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class CustomerManagementServiceTest {
    public static void main(String[] args) throws IOException {
        Path dataDirectory = Files.createTempDirectory("cinema-customers-test");
        try {
            CinemaService service = new CinemaService(new CinemaRepository(dataDirectory));
            Customer customer = service.addCustomer("Nguyen Van A", "0912345678", "a@example.com",
                    Customer.CustomerType.STUDENT);

            Customer updated = service.updateCustomer(customer.getId(), "Nguyen Van B", "0987654321",
                    "b@example.com", Customer.CustomerType.VIP);
            assertEquals("Nguyen Van B", updated.getFullName());
            assertEquals(Customer.CustomerType.VIP, updated.getType());

            CinemaService reloaded = new CinemaService(new CinemaRepository(dataDirectory));
            assertEquals("0987654321", reloaded.findCustomer(customer.getId()).getPhone());
            reloaded.deleteCustomer(customer.getId());

            CinemaService afterDelete = new CinemaService(new CinemaRepository(dataDirectory));
            assertEquals(0, afterDelete.getCustomers().size());
            System.out.println("Customer management service test passed");
        } finally {
            try (Stream<Path> paths = Files.walk(dataDirectory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }
}