package cinema.main;

import cinema.repository.CinemaRepository;
import cinema.service.CinemaService;
import cinema.ui.CinemaFrame;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                CinemaRepository repository = new CinemaRepository(Path.of("data"));
                CinemaService service = new CinemaService(repository);
                new CinemaFrame(service).setVisible(true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                     | UnsupportedLookAndFeelException | IOException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Khong the khoi dong",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}