public class CurrencyFormatTest {
    public static void main(String[] args) {
        assertEquals("180.000đ", cinema.ui.CinemaFrame.formatCurrency(180000L));
        assertEquals("0đ", cinema.ui.CinemaFrame.formatCurrency(0L));
        assertEquals("1.250.000đ", cinema.ui.CinemaFrame.formatCurrency(1250000L));
        System.out.println("Currency format test passed");
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }
}
