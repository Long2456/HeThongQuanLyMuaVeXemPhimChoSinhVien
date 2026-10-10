package cinema.ui;

import cinema.model.Customer;
import cinema.model.Movie;
import cinema.model.Seat;
import cinema.model.Showtime;
import cinema.model.Ticket;
import cinema.service.CinemaService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CinemaFrame extends JFrame {
    private static final Color SIDEBAR_BG = new Color(10, 23, 44);
    private static final Color NAV_ACTIVE_BG = new Color(44, 84, 146);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color PRIMARY_DARK = new Color(15, 76, 180);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color SOFT_TEXT = new Color(90, 102, 130);
    private static final Color BORDER = new Color(212, 218, 230);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final CinemaService service;
    private final JTextField searchField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private JPanel contentPanel;
    private final Map<String, JPanel> navigationItems = new LinkedHashMap<>();
    private final Map<String, JLabel> navigationIcons = new LinkedHashMap<>();
    private final JComboBox<Customer.CustomerType> customerTypeBox =
            new JComboBox<>(Customer.CustomerType.values());
    private final JComboBox<String> movieCombo = new JComboBox<>();
    private final JComboBox<String> showtimeCombo = new JComboBox<>();
    private final JComboBox<Customer> bookingCustomerBox = new JComboBox<>();
    private final JComboBox<Movie> bookingMovieBox = new JComboBox<>();
    private final JComboBox<Showtime> bookingShowtimeBox = new JComboBox<>();
    private final JComboBox<Seat> bookingSeatBox = new JComboBox<>();
    private final JLabel bookingTotalLabel = new JLabel();
    private Ticket lastBookedTicket;

    public CinemaFrame(CinemaService service) {
        this.service = service;
        setTitle("CineStudent");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1200, 760));
        setLocationRelativeTo(null);
        setContentPane(createMainLayout());
        refreshBookingOptions();
    }

    private JPanel createMainLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PANEL_BG);
        root.add(createSidebar(), BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(NAV_ACTIVE_BG);
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        content.add(createTopBar(), BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        content.add(contentPanel, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
        showDashboard();
        return root;
    }

    private void showDashboard() {
        JPanel centerLayout = new JPanel(new BorderLayout(18, 18));
        centerLayout.setOpaque(false);

        JPanel leftPane = new JPanel();
        leftPane.setOpaque(false);
        leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
        leftPane.add(createOverviewPanel());
        leftPane.add(Box.createVerticalStrut(14));
        leftPane.add(createMovieListPanel());
        leftPane.add(Box.createVerticalStrut(14));
        leftPane.add(createDashboardLowerRow());

        JPanel rightPane = new JPanel();
        rightPane.setOpaque(false);
        rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
        rightPane.add(createChartPanel());
        rightPane.add(Box.createVerticalStrut(18));
        rightPane.add(createShowtimePanel());

        centerLayout.add(leftPane, BorderLayout.CENTER);
        centerLayout.add(rightPane, BorderLayout.EAST);

        JPanel dashboardFrame = new RoundedPanel(new BorderLayout(), 16);
        dashboardFrame.setBackground(Color.WHITE);
        dashboardFrame.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(16, new Color(222, 232, 243), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        dashboardFrame.add(centerLayout, BorderLayout.CENTER);

        JPanel dashboardBackground = new JPanel(new BorderLayout());
        dashboardBackground.setBackground(NAV_ACTIVE_BG);
        dashboardBackground.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dashboardBackground.add(dashboardFrame, BorderLayout.CENTER);
        showScreen("Tổng quan", dashboardBackground);
    }

    private void showCustomerManagement() {
        showScreen("Quản lý khách hàng", createCustomerManagementPanel());
    }

    private void showMovieManagement() {
        showScreen("Quản lý phim", createMovieManagementPanel());
    }

    private void showShowtimeManagement() {
        showScreen("Quản lý suất chiếu", createShowtimeManagementPanel());
    }

    private void showSeatManagement() {
        showScreen("Quản lý ghế", createSeatManagementPanel());
    }

    private void showBookingScreen() {
        showScreen("Đặt vé", createBookingScreen());
    }

    private void showPaymentScreen() {
        showScreen("Thanh toán", createPaymentScreen());
    }

    private void showHistoryScreen() {
        showScreen("Lịch sử vé", createHistoryScreen());
    }

    private void showSettingsScreen() {
        showScreen("Cài đặt", createSettingsScreen());
    }

    private void showScreen(String navigationLabel, JPanel screen) {
        contentPanel.removeAll();
        contentPanel.add(screen, BorderLayout.CENTER);
        selectNavigationItem(navigationLabel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void selectNavigationItem(String selectedLabel) {
        for (Map.Entry<String, JPanel> entry : navigationItems.entrySet()) {
            boolean selected = entry.getKey().equals(selectedLabel);
            entry.getValue().setBackground(selected ? NAV_ACTIVE_BG : SIDEBAR_BG);
            navigationIcons.get(entry.getKey()).setText(selected ? "◉" : "•");
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 8, 18));

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brand.setOpaque(false);
        JLabel logo = new JLabel("◉");
        logo.setForeground(new Color(255, 255, 255));
        logo.setFont(new Font("SansSerif", Font.BOLD, 34));
        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("CineStudent");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Hệ thống quản lý vé");
        subtitle.setForeground(new Color(171, 185, 211));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textWrap.add(title);
        textWrap.add(subtitle);
        brand.add(logo);
        brand.add(textWrap);
        logoPanel.add(brand, BorderLayout.WEST);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(22, 14, 0, 14));
        nav.add(createNavItem("Tổng quan", true, () -> showDashboard()));
        nav.add(createNavItem("Quản lý khách hàng", false, () -> showCustomerManagement()));
        nav.add(createNavItem("Quản lý phim", false, () -> showMovieManagement()));
        nav.add(createNavItem("Quản lý suất chiếu", false, () -> showShowtimeManagement()));
        nav.add(createNavItem("Quản lý ghế", false, () -> showSeatManagement()));
        nav.add(createNavItem("Đặt vé", false, () -> showBookingScreen()));
        nav.add(createNavItem("Thanh toán", false, () -> showPaymentScreen()));
        nav.add(createNavItem("Lịch sử vé", false, () -> showHistoryScreen()));
        nav.add(createNavItem("Cài đặt", false, () -> showSettingsScreen()));

        sidebar.add(nav, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createNavItem(String label, boolean selected, Runnable action) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(selected ? NAV_ACTIVE_BG : SIDEBAR_BG);
        row.setMaximumSize(new Dimension(200, 44));
        row.setPreferredSize(new Dimension(200, 44));
        row.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JLabel icon = new JLabel(selected ? "◉" : "•");
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel text = new JLabel(label);
        text.setForeground(Color.WHITE);
        text.setFont(new Font("SansSerif", Font.PLAIN, 15));

        row.add(icon, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        navigationItems.put(label, row);
        navigationIcons.put(label, icon);

        java.awt.event.MouseListener clickListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                action.run();
            }
        };
        row.addMouseListener(clickListener);
        icon.addMouseListener(clickListener);
        text.addMouseListener(clickListener);
        return row;
    }

    private JPanel createCustomerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(247, 249, 252));

        JPanel leftPanel = new RoundedPanel(new BorderLayout(12, 14), 14);
        leftPanel.setBackground(CARD_BG);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(14, new Color(226, 233, 243), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);
        JLabel title = new JLabel("Quản lý khách hàng");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(20, 31, 52));

        JButton addButton = new JButton("+  Thêm khách hàng");
        addButton.setBackground(PRIMARY);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        header.add(title, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        JTextField customerSearch = new JTextField();
        customerSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        customerSearch.setToolTipText("Tìm theo tên, số điện thoại hoặc email");
        customerSearch.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, new Color(215, 226, 240), 1),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        JLabel searchIcon = new JLabel("⌕");
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        searchIcon.setForeground(SOFT_TEXT);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(customerSearch, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.SOUTH);
        leftPanel.add(top, BorderLayout.NORTH);

        String[] columns = {"Mã KH", "Họ tên", "Số điện thoại", "Email", "Loại khách hàng", "Thao tác"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Customer customer : service.getCustomers()) {
            tableModel.addRow(new Object[]{customer.getId(), customer.getFullName(), customer.getPhone(),
                customer.getEmail(), customer.getType().toString(), "Sửa     Xóa"});
        }

        JTable customerTable = new JTable(tableModel);
        customerTable.setRowHeight(44);
        customerTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        customerTable.setForeground(new Color(35, 48, 73));
        customerTable.setSelectionBackground(new Color(232, 241, 255));
        customerTable.setSelectionForeground(new Color(28, 39, 57));
        customerTable.setGridColor(new Color(235, 240, 247));
        customerTable.setShowVerticalLines(false);
        customerTable.setFillsViewportHeight(true);
        customerTable.setAutoCreateRowSorter(true);
        customerTable.getTableHeader().setReorderingAllowed(false);
        customerTable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        customerTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        customerTable.getTableHeader().setForeground(new Color(55, 66, 86));
        customerTable.getTableHeader().setBackground(new Color(245, 248, 252));
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(0, 9, 0, 9));
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        cellRenderer.setForeground(new Color(29, 43, 65));
        for (int column = 0; column < customerTable.getColumnCount(); column++) {
            customerTable.getColumnModel().getColumn(column).setCellRenderer(cellRenderer);
        }
        customerTable.getColumnModel().getColumn(4).setCellRenderer(new CustomerTypeRenderer());
        customerTable.getColumnModel().getColumn(5).setCellRenderer(new CustomerActionsRenderer());
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(115);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(105);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);
        JLabel pageInfo = new JLabel();
        pageInfo.setForeground(SOFT_TEXT);
        Runnable updateCount = () -> {
            int visibleCount = sorter.getViewRowCount();
            int totalCount = service.getCustomers().size();
            if (totalCount == 0) {
                pageInfo.setText("Chưa có khách hàng");
            } else if (visibleCount == 0) {
                pageInfo.setText("Không có khách hàng phù hợp");
            } else {
                pageInfo.setText("Hiển thị 1 - " + visibleCount + " / " + totalCount + " khách hàng");
            }
        };

        customerSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String query = customerSearch.getText().trim();
                sorter.setRowFilter(query.isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + Pattern.quote(query), 0, 1, 2, 3, 4));
                updateCount.run();
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filter();
            }
        });
        updateCount.run();

        JScrollPane tableScroll = new JScrollPane(customerTable);
        tableScroll.setBorder(new RoundedBorder(10, new Color(225, 233, 244), 1));
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setBackground(Color.WHITE);
        leftPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        footer.add(pageInfo, BorderLayout.WEST);
        leftPanel.add(footer, BorderLayout.SOUTH);

        JPanel rightSurface = new JPanel(new BorderLayout());
        rightSurface.setBackground(new Color(72, 84, 101));
        rightSurface.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));
        rightSurface.setPreferredSize(new Dimension(350, 0));

        JPanel formCard = new RoundedPanel(null, 12);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(new Color(239, 246, 255));
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, NAV_ACTIVE_BG, 2),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JPanel formHeader = new JPanel(new BorderLayout());
        formHeader.setOpaque(false);
        JLabel formTitle = new JLabel("Thêm khách hàng");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        formTitle.setForeground(NAV_ACTIVE_BG);
        JButton closeButton = new JButton("×");
        closeButton.setForeground(SOFT_TEXT);
        closeButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        formHeader.add(formTitle, BorderLayout.WEST);
        formHeader.add(closeButton, BorderLayout.EAST);
        formCard.add(formHeader);
        formCard.add(Box.createVerticalStrut(12));

        JTextField idField = new JTextField("Tự động tạo khi lưu");
        idField.setEditable(false);
        idField.setForeground(SOFT_TEXT);
        formCard.add(createInput("Mã khách hàng", idField));
        formCard.add(Box.createVerticalStrut(7));
        formCard.add(createInput("Họ tên *", nameField));
        formCard.add(Box.createVerticalStrut(7));
        formCard.add(createInput("Số điện thoại *", phoneField));
        formCard.add(Box.createVerticalStrut(7));
        formCard.add(createInput("Email *", emailField));
        formCard.add(Box.createVerticalStrut(7));
        formCard.add(createInputRow("Loại khách hàng *", customerTypeBox));
        formCard.add(Box.createVerticalStrut(16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton cancel = new JButton("Hủy");
        JButton save = new JButton("Lưu khách hàng");
        cancel.setFocusPainted(false);
        save.setBackground(PRIMARY);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        actions.add(cancel);
        actions.add(save);
        formCard.add(actions);
        rightSurface.add(formCard, BorderLayout.NORTH);
        rightSurface.setVisible(false);

        String[] editingCustomerId = {null};
        Runnable clearForm = () -> {
            editingCustomerId[0] = null;
            nameField.setText("");
            phoneField.setText("");
            emailField.setText("");
            customerTypeBox.setSelectedIndex(0);
            idField.setText("Tự động tạo khi lưu");
            formTitle.setText("Thêm khách hàng");
        };
        Runnable hideForm = () -> {
            clearForm.run();
            rightSurface.setVisible(false);
            panel.revalidate();
            panel.repaint();
        };
        addButton.addActionListener(event -> {
            clearForm.run();
            rightSurface.setVisible(true);
            panel.revalidate();
            panel.repaint();
            nameField.requestFocusInWindow();
        });
        closeButton.addActionListener(event -> hideForm.run());
        cancel.addActionListener(event -> hideForm.run());

        java.util.function.Consumer<Customer> editCustomer = customer -> {
            editingCustomerId[0] = customer.getId();
            idField.setText(customer.getId());
            formTitle.setText("Sửa thông tin khách hàng");
            nameField.setText(customer.getFullName());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail());
            customerTypeBox.setSelectedItem(customer.getType());
            rightSurface.setVisible(true);
            panel.revalidate();
            panel.repaint();
            nameField.requestFocusInWindow();
        };
        java.util.function.Consumer<String> deleteCustomer = customerId -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Xóa khách hàng này khỏi danh sách và dữ liệu đã lưu?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                service.deleteCustomer(customerId);
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    if (customerId.equals(tableModel.getValueAt(row, 0))) {
                        tableModel.removeRow(row);
                        break;
                    }
                }
                updateCount.run();
                refreshBookingOptions();
            } catch (IllegalArgumentException | java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể xóa khách hàng",
                        JOptionPane.ERROR_MESSAGE);
            }
        };
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                int viewRow = customerTable.rowAtPoint(event.getPoint());
                int viewColumn = customerTable.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != 5) {
                    return;
                }
                int modelRow = customerTable.convertRowIndexToModel(viewRow);
                String customerId = String.valueOf(tableModel.getValueAt(modelRow, 0));
                Customer customer = service.findCustomer(customerId);
                if (customer == null) {
                    return;
                }
                java.awt.Rectangle cellBounds = customerTable.getCellRect(viewRow, viewColumn, true);
                if (event.getX() < cellBounds.x + cellBounds.width / 2) {
                    editCustomer.accept(customer);
                } else {
                    deleteCustomer.accept(customerId);
                }
            }
        });
        save.addActionListener(event -> {
            try {
                Customer customer;
                if (editingCustomerId[0] == null) {
                    customer = service.addCustomer(nameField.getText(), phoneField.getText(),
                            emailField.getText(), (Customer.CustomerType) customerTypeBox.getSelectedItem());
                    tableModel.addRow(new Object[]{customer.getId(), customer.getFullName(), customer.getPhone(),
                        customer.getEmail(), customer.getType().toString(), "Sửa     Xóa"});
                } else {
                    customer = service.updateCustomer(editingCustomerId[0], nameField.getText(),
                            phoneField.getText(), emailField.getText(),
                            (Customer.CustomerType) customerTypeBox.getSelectedItem());
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        if (customer.getId().equals(tableModel.getValueAt(row, 0))) {
                            tableModel.setValueAt(customer.getFullName(), row, 1);
                            tableModel.setValueAt(customer.getPhone(), row, 2);
                            tableModel.setValueAt(customer.getEmail(), row, 3);
                            tableModel.setValueAt(customer.getType().toString(), row, 4);
                            break;
                        }
                    }
                }
                updateCount.run();
                refreshBookingOptions();
                hideForm.run();
            } catch (IllegalArgumentException | java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Không thể lưu khách hàng",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightSurface, BorderLayout.EAST);
        return panel;
    }

    private JPanel createMovieManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout(12, 12));
        leftPanel.setBackground(CARD_BG);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Quản lý phim");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        titleRow.add(title, BorderLayout.WEST);

        JButton addButton = new JButton("+ Thêm phim");
        addButton.setBackground(PRIMARY);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        titleRow.add(addButton, BorderLayout.EAST);

        leftPanel.add(titleRow, BorderLayout.NORTH);

        JPanel table = new JPanel(new GridLayout(0, 7, 8, 8));
        table.setOpaque(false);
        String[] columns = {"Mã phim", "Tên phim", "Thể loại", "Thời lượng", "Độ tuổi", "Trạng thái", "Hành động"};
        for (String column : columns) {
            JLabel cell = new JLabel(column, SwingConstants.CENTER);
            cell.setFont(new Font("SansSerif", Font.BOLD, 12));
            cell.setForeground(new Color(55, 66, 86));
            table.add(cell);
        }

        Object[][] rows = {
            {"P001", "Avatar 3", "Hành động", "162 phút", "13+", "Đang chiếu", "Sửa"},
            {"P002", "Superman", "Hành động", "130 phút", "13+", "Đang chiếu", "Sửa"},
            {"P003", "Lật Mặt 8", "Hài", "120 phút", "16+", "Đang chiếu", "Sửa"},
            {"P004", "Conan Movie 28", "Hoạt hình", "111 phút", "13+", "Đang chiếu", "Sửa"},
            {"P005", "Deadpool & Wolverine", "Hành động", "127 phút", "18+", "Đang chiếu", "Sửa"}
        };

        for (Object[] row : rows) {
            for (Object value : row) {
                JLabel cell = new JLabel(String.valueOf(value));
                cell.setFont(new Font("SansSerif", Font.PLAIN, 12));
                cell.setForeground(new Color(28, 39, 57));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                table.add(cell);
            }
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        leftPanel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(new JLabel("Hiển thị 1 - 5 / 10 phim"));
        leftPanel.add(footer, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(CARD_BG);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Thêm phim");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        formTitle.setForeground(new Color(22, 33, 55));
        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(18));

        rightPanel.add(createInput("Mã phim", new JTextField("P006")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Tên phim", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Thể loại", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Thời lượng", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Độ tuổi", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(20));

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang chiếu", "Ngừng chiếu"});
        JPanel statusRow = new JPanel(new BorderLayout(8, 0));
        statusRow.setOpaque(false);
        JLabel statusLabel = new JLabel("Trạng thái");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusRow.add(statusLabel, BorderLayout.NORTH);
        statusRow.add(statusBox, BorderLayout.CENTER);
        rightPanel.add(statusRow);
        rightPanel.add(Box.createVerticalStrut(20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton cancel = new JButton("Hủy");
        JButton save = new JButton("Lưu phim");
        save.setBackground(PRIMARY);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        cancel.setFocusPainted(false);
        actions.add(cancel);
        actions.add(save);
        rightPanel.add(actions);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createShowtimeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout(12, 12));
        leftPanel.setBackground(CARD_BG);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Quản lý suất chiếu");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        titleRow.add(title, BorderLayout.WEST);

        JButton addButton = new JButton("+ Thêm suất");
        addButton.setBackground(PRIMARY);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        titleRow.add(addButton, BorderLayout.EAST);

        leftPanel.add(titleRow, BorderLayout.NORTH);

        JPanel table = new JPanel(new GridLayout(0, 8, 8, 8));
        table.setOpaque(false);
        String[] columns = {"Mã suất", "Phim", "Phòng", "Ngày chiếu", "Giờ bắt đầu", "Giờ kết thúc", "Trạng thái", "Hành động"};
        for (String column : columns) {
            JLabel cell = new JLabel(column, SwingConstants.CENTER);
            cell.setFont(new Font("SansSerif", Font.BOLD, 12));
            cell.setForeground(new Color(55, 66, 86));
            table.add(cell);
        }

        Object[][] rows = {
            {"SC001", "Avatar 3", "Phòng 1", "06/15/2025", "13:00", "15:20", "Đang chiếu", "Sửa"},
            {"SC002", "Superman", "Phòng 2", "06/15/2025", "15:30", "17:20", "Đang chiếu", "Sửa"},
            {"SC003", "Lật mặt 8", "Phòng 3", "06/15/2025", "19:00", "20:40", "Đang chiếu", "Sửa"},
            {"SC004", "Conan Movie 28", "Phòng 1", "06/16/2025", "21:30", "23:10", "Đang chiếu", "Sửa"},
            {"SC005", "Deadpool & Wolverine", "Phòng 4", "06/16/2025", "22:00", "23:55", "Đang chiếu", "Sửa"}
        };

        for (Object[] row : rows) {
            for (Object value : row) {
                JLabel cell = new JLabel(String.valueOf(value));
                cell.setFont(new Font("SansSerif", Font.PLAIN, 12));
                cell.setForeground(new Color(28, 39, 57));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                table.add(cell);
            }
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        leftPanel.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(new JLabel("Hiển thị 1 - 5 / 15 suất"));
        leftPanel.add(footer, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(CARD_BG);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Thêm suất chiếu");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        formTitle.setForeground(new Color(22, 33, 55));
        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(18));

        rightPanel.add(createInput("Mã suất", new JTextField("SC006")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Phim", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Phòng chiếu", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Ngày chiếu", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Giờ bắt đầu", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Giờ kết thúc", new JTextField("")));
        rightPanel.add(Box.createVerticalStrut(20));

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang chiếu", "Ngừng chiếu"});
        JPanel statusRow = new JPanel(new BorderLayout(8, 0));
        statusRow.setOpaque(false);
        JLabel statusLabel = new JLabel("Trạng thái");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusRow.add(statusLabel, BorderLayout.NORTH);
        statusRow.add(statusBox, BorderLayout.CENTER);
        rightPanel.add(statusRow);
        rightPanel.add(Box.createVerticalStrut(20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton cancel = new JButton("Hủy");
        JButton save = new JButton("Lưu suất");
        save.setBackground(PRIMARY);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        cancel.setFocusPainted(false);
        actions.add(cancel);
        actions.add(save);
        rightPanel.add(actions);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createSeatManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout(12, 12));
        leftPanel.setBackground(CARD_BG);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Quản lý ghế");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        titleRow.add(title, BorderLayout.WEST);

        JButton addButton = new JButton("+ Thêm ghế");
        addButton.setBackground(PRIMARY);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        titleRow.add(addButton, BorderLayout.EAST);

        leftPanel.add(titleRow, BorderLayout.NORTH);

        JPanel seatMap = new JPanel();
        seatMap.setBackground(CARD_BG);
        seatMap.setLayout(new GridLayout(8, 10, 10, 10));
        seatMap.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[][] seatStates = {
            {"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10"},
            {"B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10"},
            {"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "C10"},
            {"D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D10"},
            {"E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10"},
            {"F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10"},
            {"G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "G10"},
            {"H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9", "H10"}
        };

        for (int row = 0; row < seatStates.length; row++) {
            for (int col = 0; col < seatStates[row].length; col++) {
                JButton seat = new JButton(seatStates[row][col]);
                seat.setFocusPainted(false);
                seat.setPreferredSize(new Dimension(34, 34));
                if ((row + col) % 3 == 0) {
                    seat.setBackground(new Color(239, 68, 68));
                    seat.setForeground(Color.WHITE);
                } else if ((row + col) % 3 == 1) {
                    seat.setBackground(new Color(34, 197, 94));
                    seat.setForeground(Color.WHITE);
                } else {
                    seat.setBackground(new Color(59, 130, 246));
                    seat.setForeground(Color.WHITE);
                }
                seatMap.add(seat);
            }
        }

        leftPanel.add(seatMap, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(new JLabel("Trạng thái:  0 = trống   1 = đã đặt   2 = đang chọn"));
        leftPanel.add(footer, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(CARD_BG);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Thông tin ghế");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        formTitle.setForeground(new Color(22, 33, 55));
        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(18));

        rightPanel.add(createInput("Mã ghế", new JTextField("A5")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Phòng chiếu", new JTextField("Phòng 1")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Loại ghế", new JTextField("Thường")));
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createInput("Giá ghế", new JTextField("100000")));
        rightPanel.add(Box.createVerticalStrut(20));

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Trống", "Đã đặt", "Đang chọn"});
        JPanel statusRow = new JPanel(new BorderLayout(8, 0));
        statusRow.setOpaque(false);
        JLabel statusLabel = new JLabel("Trạng thái");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusRow.add(statusLabel, BorderLayout.NORTH);
        statusRow.add(statusBox, BorderLayout.CENTER);
        rightPanel.add(statusRow);
        rightPanel.add(Box.createVerticalStrut(20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton cancel = new JButton("Hủy");
        JButton save = new JButton("Lưu ghế");
        save.setBackground(PRIMARY);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        cancel.setFocusPainted(false);
        actions.add(cancel);
        actions.add(save);
        rightPanel.add(actions);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createBookingScreen() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Đặt vé");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(18));

        formPanel.add(createInputRow("Khách hàng", bookingCustomerBox));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createInputRow("Phim", bookingMovieBox));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createInputRow("Suất chiếu", bookingShowtimeBox));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createInputRow("Ghế", bookingSeatBox));
        formPanel.add(Box.createVerticalStrut(18));

        JPanel summaryBox = new JPanel();
        summaryBox.setBackground(new Color(247, 248, 252));
        summaryBox.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        summaryBox.setLayout(new BoxLayout(summaryBox, BoxLayout.Y_AXIS));
        summaryBox.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel totalLabel = new JLabel("Tổng tiền");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bookingTotalLabel.setText(formatCurrency(0L));
        bookingTotalLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        bookingTotalLabel.setForeground(new Color(37, 99, 235));

        summaryBox.add(totalLabel);
        summaryBox.add(Box.createVerticalStrut(8));
        summaryBox.add(bookingTotalLabel);

        formPanel.add(summaryBox);
        formPanel.add(Box.createVerticalStrut(18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton cancel = new JButton("Hủy");
        JButton book = new JButton("Xác nhận đặt vé");
        book.setBackground(PRIMARY);
        book.setForeground(Color.WHITE);
        book.setFocusPainted(false);
        cancel.setFocusPainted(false);
        cancel.addActionListener(event -> refreshBookingOptions());
        book.addActionListener(event -> confirmBooking());
        actions.add(cancel);
        actions.add(book);
        formPanel.add(actions);

        bookingMovieBox.addActionListener(event -> updateBookingShowtimes());
        bookingShowtimeBox.addActionListener(event -> updateBookingSeats());
        bookingSeatBox.addActionListener(event -> updateBookingTotal());
        updateBookingShowtimes();
        updateBookingSeats();
        updateBookingTotal();

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPaymentScreen() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(CARD_BG);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Thanh toán");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(18));

        JPanel ticketInfo = new JPanel();
        ticketInfo.setLayout(new BoxLayout(ticketInfo, BoxLayout.Y_AXIS));
        ticketInfo.setBackground(new Color(247, 248, 252));
        ticketInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel ticketHeader = new JLabel("Thông tin vé");
        ticketHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        ticketHeader.setForeground(new Color(20, 31, 52));
        ticketInfo.add(ticketHeader);
        ticketInfo.add(Box.createVerticalStrut(12));
        ticketInfo.add(new JLabel("Khách hàng: Nguyễn Văn A"));
        ticketInfo.add(Box.createVerticalStrut(8));
        ticketInfo.add(new JLabel("Phim: Avatar 3"));
        ticketInfo.add(Box.createVerticalStrut(8));
        ticketInfo.add(new JLabel("Phòng: Phòng 1"));
        ticketInfo.add(Box.createVerticalStrut(8));
        ticketInfo.add(new JLabel("Ghế: A5, A6"));
        ticketInfo.add(Box.createVerticalStrut(8));
        ticketInfo.add(new JLabel("Thời gian: 13:00 - 15:20"));

        leftPanel.add(ticketInfo);
        leftPanel.add(Box.createVerticalStrut(18));

        JPanel paymentMethod = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        paymentMethod.setOpaque(false);
        JButton cash = new JButton("Tiền mặt");
        JButton transfer = new JButton("Chuyển khoản");
        JButton ewallet = new JButton("Ví điện tử");
        cash.setBackground(new Color(34, 197, 94));
        cash.setForeground(Color.WHITE);
        transfer.setBackground(new Color(59, 130, 246));
        transfer.setForeground(Color.WHITE);
        ewallet.setBackground(new Color(245, 158, 11));
        ewallet.setForeground(Color.WHITE);
        cash.setFocusPainted(false);
        transfer.setFocusPainted(false);
        ewallet.setFocusPainted(false);
        paymentMethod.add(cash);
        paymentMethod.add(transfer);
        paymentMethod.add(ewallet);
        leftPanel.add(paymentMethod);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(CARD_BG);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel sumTitle = new JLabel("Chi tiết thanh toán");
        sumTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        sumTitle.setForeground(new Color(20, 31, 52));
        rightPanel.add(sumTitle);
        rightPanel.add(Box.createVerticalStrut(18));

        JPanel amount = new JPanel(new GridLayout(4, 2, 10, 10));
        amount.setOpaque(false);
        amount.add(new JLabel("Tổng tiền vé:"));
        amount.add(new JLabel("180.000đ"));
        amount.add(new JLabel("Giảm giá:"));
        amount.add(new JLabel("0đ"));
        amount.add(new JLabel("Phí dịch vụ:"));
        amount.add(new JLabel("0đ"));
        amount.add(new JLabel("Tổng thanh toán:"));
        amount.add(new JLabel("180.000đ"));
        rightPanel.add(amount);
        rightPanel.add(Box.createVerticalStrut(18));

        JButton payButton = new JButton("Thanh toán thành công");
        payButton.setBackground(PRIMARY);
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);
        payButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(payButton);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createHistoryScreen() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(CARD_BG);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Lịch sử vé");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        content.add(title, BorderLayout.NORTH);

        JPanel table = new JPanel(new GridLayout(0, 6, 10, 10));
        table.setOpaque(false);
        String[] columns = {"Mã vé", "Khách hàng", "Phim", "Ghế", "Thời gian", "Trạng thái"};
        for (String column : columns) {
            JLabel cell = new JLabel(column, SwingConstants.CENTER);
            cell.setFont(new Font("SansSerif", Font.BOLD, 12));
            cell.setForeground(new Color(55, 66, 86));
            table.add(cell);
        }

        for (Ticket ticket : service.getTickets()) {
            Customer customer = service.findCustomer(ticket.getCustomerId());
            Showtime showtime = service.findShowtime(ticket.getShowtimeId());
            table.add(createHistoryCell(ticket.getId()));
            table.add(createHistoryCell(customer == null ? ticket.getCustomerId() : customer.getFullName()));
            table.add(createHistoryCell(showtime == null ? "" : showtime.getMovie().getTitle()));
            table.add(createHistoryCell(ticket.getSeatId()));
            table.add(createHistoryCell(showtime == null ? "" : showtime.getStartTime().format(TIME_FORMATTER)));
            table.add(createHistoryCell(ticket.isPaid() ? "Đã thanh toán" : "Chưa thanh toán"));
        }

        if (service.getTickets().isEmpty()) {
            JLabel empty = new JLabel("Chưa có vé nào được đặt.");
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            empty.setForeground(SOFT_TEXT);
            table.add(empty);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        content.add(scroll, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSettingsScreen() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BG);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Cài đặt hệ thống");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(20, 31, 52));
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        content.add(createInput("Tên rạp", new JTextField("CineStudent")));
        content.add(Box.createVerticalStrut(10));
        content.add(createInput("Địa chỉ", new JTextField("123 Đường ABC, Quận 1")));
        content.add(Box.createVerticalStrut(10));
        content.add(createInput("Số điện thoại", new JTextField("0912345678")));
        content.add(Box.createVerticalStrut(20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton save = new JButton("Lưu thay đổi");
        save.setBackground(PRIMARY);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        actions.add(save);
        content.add(actions);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(true);
        bar.setBackground(NAV_ACTIVE_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setPreferredSize(new Dimension(420, 42));
        searchWrap.setBackground(NAV_ACTIVE_BG);
        searchWrap.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, new Color(111, 145, 195), 1),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        JLabel searchIcon = new JLabel("⌕");
        searchIcon.setForeground(new Color(224, 235, 252));
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setText("Tìm kiếm phim, khách hàng, mã vé...");
        searchField.setForeground(new Color(224, 235, 252));
        searchWrap.add(searchIcon, BorderLayout.WEST);
        searchWrap.add(searchField, BorderLayout.CENTER);

        JPanel userWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        userWrap.setOpaque(false);
        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 22));
        JLabel avatar = new JLabel("◉");
        avatar.setFont(new Font("SansSerif", Font.BOLD, 26));
        avatar.setForeground(new Color(255, 255, 255));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(63, 116, 206));
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel userBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userBox.setOpaque(false);
        JLabel userName = new JLabel("Nguyễn Văn A");
        userName.setFont(new Font("SansSerif", Font.BOLD, 15));
        userName.setForeground(Color.WHITE);
        JLabel role = new JLabel("Quản trị viên");
        role.setForeground(new Color(210, 224, 246));
        role.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        userInfo.add(userName);
        userInfo.add(role);
        userBox.add(userInfo);
        userBox.add(avatar);
        userWrap.add(bell);
        userWrap.add(userBox);

        bar.add(searchWrap, BorderLayout.WEST);
        bar.add(userWrap, BorderLayout.EAST);
        return bar;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new RoundedPanel(new BorderLayout(0, 16), 16);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(16, new Color(222, 232, 243), 1),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel("Tổng quan");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(20, 31, 52));

        JLabel subtitle = new JLabel("Chào mừng bạn trở lại! Đây là tổng quan hoạt động của hệ thống.");
        subtitle.setForeground(SOFT_TEXT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitle);

        JPanel statGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        statGrid.setOpaque(false);
        statGrid.add(createStatCard("12", "Phim đang chiếu", "▲ 2 so với tuần trước", new Color(62, 120, 255), new Color(234, 242, 255)));
        statGrid.add(createStatCard("36", "Suất chiếu hôm nay", "▲ 5 so với hôm qua", new Color(124, 86, 247), new Color(238, 233, 255)));
        statGrid.add(createStatCard("245", "Vé đã bán", "▲ 12% so với tuần trước", new Color(39, 175, 110), new Color(227, 247, 234)));
        statGrid.add(createStatCard("18.500.000đ", "Doanh thu hôm nay", "▲ 15% so với tuần trước", new Color(245, 140, 66), new Color(255, 239, 229)));

        panel.add(textPanel, BorderLayout.NORTH);
        panel.add(statGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String value, String title, String trend, Color borderColor, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, borderColor, 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setOpaque(true);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueLabel.setForeground(new Color(17, 24, 39));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(58, 70, 89));
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel trendLabel = new JLabel(trend);
        trendLabel.setForeground(new Color(78, 96, 116));
        trendLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(valueLabel);
        text.add(Box.createVerticalStrut(4));
        text.add(titleLabel);
        text.add(Box.createVerticalStrut(4));
        text.add(trendLabel);

        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMovieListPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);

        JLabel title = new JLabel("Phim đang chiếu");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(20, 31, 52));
        panel.add(title, BorderLayout.NORTH);

        JPanel movieGrid = new JPanel(new GridLayout(1, 5, 16, 0));
        movieGrid.setOpaque(false);
        for (Movie movie : service.getMovies()) {
            movieGrid.add(createMovieCard(movie));
        }

        panel.add(movieGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, BORDER, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        card.setOpaque(true);

        JLabel poster = new JLabel(movie.getTitle(), SwingConstants.CENTER);
        poster.setOpaque(true);
        poster.setBackground(createPosterColor(movie.getTitle()));
        poster.setForeground(Color.WHITE);
        poster.setFont(new Font("SansSerif", Font.BOLD, 18));
        poster.setPreferredSize(new Dimension(0, 170));
        poster.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrap.setOpaque(false);
        JLabel badge = new JLabel("Đang chiếu");
        badge.setFont(new Font("SansSerif", Font.PLAIN, 11));
        badge.setForeground(new Color(25, 125, 61));
        badge.setBackground(new Color(220, 252, 231));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        badgeWrap.add(badge);

        JLabel name = new JLabel(movie.getTitle());
        name.setFont(new Font("SansSerif", Font.BOLD, 18));
        name.setForeground(new Color(28, 39, 57));

        JLabel meta = new JLabel("Thời lượng: " + movie.getDurationMinutes() + " phút");
        meta.setForeground(SOFT_TEXT);
        meta.setFont(new Font("SansSerif", Font.PLAIN, 13));

        String ageText = movie.getAgeLimit() == 0 ? "Mọi lứa tuổi" : movie.getAgeLimit() + "+";
        JLabel age = new JLabel("Độ tuổi: " + ageText);
        age.setForeground(SOFT_TEXT);
        age.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(name);
        info.add(Box.createVerticalStrut(6));
        info.add(meta);
        info.add(age);

        JButton button = new JButton("Đặt vé");
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(120, 36));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.add(button);

        JPanel content = new JPanel(new BorderLayout(10, 12));
        content.setOpaque(false);
        content.add(info, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        card.add(poster, BorderLayout.NORTH);
        card.add(badgeWrap, BorderLayout.CENTER);
        card.add(content, BorderLayout.SOUTH);
        return card;
    }

    private Color createPosterColor(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("avatar")) return new Color(49, 143, 208);
        if (lower.contains("superman")) return new Color(217, 120, 138);
        if (lower.contains("lật")) return new Color(91, 125, 214);
        if (lower.contains("conan")) return new Color(128, 110, 57);
        return new Color(130, 162, 253);
    }

    private JPanel createCustomerFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel heading = new JLabel("Thông tin khách hàng");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(new Color(22, 33, 55));
        formPanel.add(heading);
        formPanel.add(Box.createVerticalStrut(18));

        formPanel.add(createInput("Họ tên", nameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createInput("Số điện thoại", phoneField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createInput("Email", emailField));
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(createInputRow("Loại khách", customerTypeBox));
        formPanel.add(Box.createVerticalStrut(18));

        JButton createCustomerButton = new JButton("Tạo khách hàng");
        createCustomerButton.setBackground(PRIMARY);
        createCustomerButton.setForeground(Color.WHITE);
        createCustomerButton.setFocusPainted(false);
        createCustomerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        createCustomerButton.setPreferredSize(new Dimension(180, 40));
        createCustomerButton.addActionListener(event -> createCustomer());
        formPanel.add(createCustomerButton);

        return formPanel;
    }

    private JPanel createInput(String labelText, JTextField field) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(240, 54));
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(42, 55, 75));
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.add(label, BorderLayout.NORTH);
        field.setPreferredSize(new Dimension(0, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel createInputRow(String labelText, Component field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(240, 54));
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(42, 55, 75));
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.add(label, BorderLayout.NORTH);
        JPanel value = new JPanel(new BorderLayout());
        value.setOpaque(false);
        field.setPreferredSize(new Dimension(200, 36));
        value.add(field, BorderLayout.CENTER);
        row.add(value, BorderLayout.CENTER);
        return row;
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, new Color(222, 228, 238), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        panel.setLayout(new BorderLayout(10, 12));

        JLabel title = new JLabel("Doanh thu theo ngày");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(20, 31, 52));
        panel.add(title, BorderLayout.NORTH);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 244, 251));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(83, 112, 255));
                int[] values = {100, 130, 150, 110, 170, 140, 200};
                int startX = 24;
                int baseY = getHeight() - 22;
                int barWidth = 18;
                int gap = 12;
                for (int i = 0; i < values.length; i++) {
                    int height = values[i] * 2 / 3;
                    int x = startX + i * (barWidth + gap);
                    int y = baseY - height;
                    g.fillRoundRect(x, y, barWidth, height, 8, 8);
                }
            }
        };
        chart.setPreferredSize(new Dimension(260, 200));
        chart.setBackground(new Color(244, 247, 251));
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDashboardLowerRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.add(createUpcomingSchedulePanel());
        row.add(createRecentBookingsPanel());
        return row;
    }

    private JPanel createUpcomingSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, new Color(222, 228, 238), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Lịch chiếu sắp tới");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(20, 31, 52));
        panel.add(title, BorderLayout.NORTH);

        JPanel table = new JPanel(new GridLayout(0, 4, 12, 10));
        table.setOpaque(false);
        table.add(createMiniTableCell("Thời gian", true));
        table.add(createMiniTableCell("Phim", true));
        table.add(createMiniTableCell("Phòng", true));
        table.add(createMiniTableCell("Trạng thái", true));

        for (Showtime showtime : service.getShowtimes()) {
            table.add(createMiniTableCell(showtime.getStartTime().format(TIME_FORMATTER), false));
            table.add(createMiniTableCell(showtime.getMovie().getTitle(), false));
            table.add(createMiniTableCell(showtime.getRoomName(), false));
            table.add(createMiniTableCell("Đang chiếu", false));
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRecentBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 10));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, new Color(222, 228, 238), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Vé đặt gần đây");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(20, 31, 52));
        panel.add(title, BorderLayout.NORTH);

        JPanel table = new JPanel(new GridLayout(0, 5, 12, 10));
        table.setOpaque(false);
        table.add(createMiniTableCell("Mã vé", true));
        table.add(createMiniTableCell("Khách hàng", true));
        table.add(createMiniTableCell("Phim", true));
        table.add(createMiniTableCell("Thời gian", true));
        table.add(createMiniTableCell("Trạng thái", true));

        java.util.List<Ticket> tickets = service.getTickets();
        int startIndex = Math.max(0, tickets.size() - 4);
        for (int i = tickets.size() - 1; i >= startIndex; i--) {
            Ticket ticket = tickets.get(i);
            Customer customer = service.findCustomer(ticket.getCustomerId());
            Showtime showtime = service.findShowtime(ticket.getShowtimeId());
            table.add(createMiniTableCell(ticket.getId(), false));
            table.add(createMiniTableCell(customer == null ? "" : customer.getFullName(), false));
            table.add(createMiniTableCell(showtime == null ? "" : showtime.getMovie().getTitle(), false));
            table.add(createMiniTableCell(showtime == null ? "" : showtime.getStartTime().format(TIME_FORMATTER), false));
            table.add(createMiniTableCell(ticket.isPaid() ? "Đã thanh toán" : "Chưa thanh toán", false));
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMiniTableCell(String text, boolean header) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(header ? new Color(245, 248, 252) : Color.WHITE);
        cell.setBorder(BorderFactory.createLineBorder(new Color(222, 228, 238), 1));
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", header ? Font.BOLD : Font.PLAIN, 12));
        label.setForeground(header ? new Color(55, 66, 86) : new Color(35, 48, 73));
        cell.add(label, BorderLayout.CENTER);
        return cell;
    }

    private JPanel createShowtimePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, new Color(222, 228, 238), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        panel.setLayout(new BorderLayout(10, 12));

        JLabel title = new JLabel("Suất chiếu gần nhất");
        title.setFont(new Font("SansSerif", Font.BOLD, 21));
        title.setForeground(new Color(20, 31, 52));
        panel.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        for (Showtime showtime : service.getShowtimes()) {
            list.add(createShowtimeRow(showtime));
            list.add(Box.createVerticalStrut(10));
        }

        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createShowtimeRow(Showtime showtime) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(247, 248, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel(showtime.getMovie().getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(30, 40, 60));

        JLabel time = new JLabel(showtime.getStartTime().format(TIME_FORMATTER) + " • " + showtime.getRoomName());
        time.setFont(new Font("SansSerif", Font.PLAIN, 12));
        time.setForeground(SOFT_TEXT);

        JLabel status = new JLabel("Sắp chiếu");
        status.setForeground(new Color(59, 130, 246));
        status.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(time);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(status);

        row.add(left, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel createShowtimeSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(18, new Color(222, 228, 238), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tóm tắt nhanh");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(20, 31, 52));
        panel.add(title);
        panel.add(Box.createVerticalStrut(14));

        JPanel stat1 = new JPanel(new BorderLayout(8, 8));
        stat1.setOpaque(false);
        stat1.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        stat1.setBackground(new Color(237, 245, 255));
        JLabel label1 = new JLabel("Suất chiếu trong tuần");
        label1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label1.setForeground(SOFT_TEXT);
        JLabel value1 = new JLabel("18");
        value1.setFont(new Font("SansSerif", Font.BOLD, 26));
        value1.setForeground(new Color(37, 99, 235));
        stat1.add(label1, BorderLayout.WEST);
        stat1.add(value1, BorderLayout.EAST);
        panel.add(stat1);
        panel.add(Box.createVerticalStrut(10));

        JPanel stat2 = new JPanel(new BorderLayout(8, 8));
        stat2.setOpaque(false);
        stat2.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        stat2.setBackground(new Color(235, 248, 240));
        JLabel label2 = new JLabel("Vé trong ngày");
        label2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label2.setForeground(SOFT_TEXT);
        JLabel value2 = new JLabel(String.valueOf(service.getTickets().size()));
        value2.setFont(new Font("SansSerif", Font.BOLD, 26));
        value2.setForeground(new Color(22, 163, 74));
        stat2.add(label2, BorderLayout.WEST);
        stat2.add(value2, BorderLayout.EAST);
        panel.add(stat2);

        return panel;
    }

    private JPanel createMiniCell(String text) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBackground(new Color(255, 255, 255));
        cell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        cell.setPreferredSize(new Dimension(0, 40));
        JLabel label = new JLabel(text);
        label.setForeground(new Color(35, 48, 73));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cell.add(label, BorderLayout.CENTER);
        return cell;
    }

    private JLabel createHistoryCell(String text) {
        JLabel cell = new JLabel(text, SwingConstants.CENTER);
        cell.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cell.setForeground(new Color(28, 39, 57));
        return cell;
    }

    public static String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }

    private void refreshBookingOptions() {
        bookingCustomerBox.removeAllItems();
        for (Customer customer : service.getCustomers()) {
            bookingCustomerBox.addItem(customer);
        }
        bookingMovieBox.removeAllItems();
        for (Movie movie : service.getMovies()) {
            bookingMovieBox.addItem(movie);
        }
        updateBookingShowtimes();
        updateBookingSeats();
        updateBookingTotal();
    }

    private void updateBookingShowtimes() {
        bookingShowtimeBox.removeAllItems();
        Movie selectedMovie = (Movie) bookingMovieBox.getSelectedItem();
        for (Showtime showtime : service.getShowtimes()) {
            if (selectedMovie == null || showtime.getMovie().equals(selectedMovie)) {
                bookingShowtimeBox.addItem(showtime);
            }
        }
        if (bookingShowtimeBox.getItemCount() > 0) {
            bookingShowtimeBox.setSelectedIndex(0);
        }
        updateBookingSeats();
    }

    private void updateBookingSeats() {
        bookingSeatBox.removeAllItems();
        Showtime selectedShowtime = (Showtime) bookingShowtimeBox.getSelectedItem();
        if (selectedShowtime == null) {
            return;
        }
        List<Seat> availableSeats = service.getAvailableSeats(selectedShowtime);
        for (Seat seat : availableSeats) {
            bookingSeatBox.addItem(seat);
        }
        if (bookingSeatBox.getItemCount() > 0) {
            bookingSeatBox.setSelectedIndex(0);
        }
        updateBookingTotal();
    }

    private void updateBookingTotal() {
        Customer customer = (Customer) bookingCustomerBox.getSelectedItem();
        Showtime showtime = (Showtime) bookingShowtimeBox.getSelectedItem();
        Seat seat = (Seat) bookingSeatBox.getSelectedItem();
        if (customer == null || showtime == null || seat == null) {
            bookingTotalLabel.setText(formatCurrency(0L));
            return;
        }
        bookingTotalLabel.setText(formatCurrency(service.calculatePrice(customer, showtime, seat)));
    }

    private void confirmBooking() {
        Customer customer = (Customer) bookingCustomerBox.getSelectedItem();
        Showtime showtime = (Showtime) bookingShowtimeBox.getSelectedItem();
        Seat seat = (Seat) bookingSeatBox.getSelectedItem();
        if (customer == null || showtime == null || seat == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin khách hàng, suất chiếu và ghế.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Ticket ticket = service.bookTicket(customer, showtime, seat);
            lastBookedTicket = ticket;
            JOptionPane.showMessageDialog(this, "Đặt vé thành công. Mã vé: " + ticket.getId(),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            refreshBookingOptions();
            showHistoryScreen();
        } catch (IllegalArgumentException | java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đặt vé", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class CustomerTypeRenderer extends JPanel
            implements javax.swing.table.TableCellRenderer {
        private final JLabel badge = new JLabel("", SwingConstants.CENTER);

        CustomerTypeRenderer() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(true);
            badge.setOpaque(true);
            badge.setFont(new Font("SansSerif", Font.BOLD, 11));
            badge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            add(badge);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            String type = String.valueOf(value);
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            badge.setText(type);
            if (type.contains("Sinh viên")) {
                badge.setBackground(new Color(241, 231, 255));
                badge.setForeground(new Color(111, 55, 176));
            } else if (type.contains("VIP")) {
                badge.setBackground(new Color(221, 247, 241));
                badge.setForeground(new Color(20, 120, 102));
            } else {
                badge.setBackground(new Color(255, 240, 220));
                badge.setForeground(new Color(172, 98, 20));
            }
            return this;
        }
    }

    private static final class CustomerActionsRenderer extends JPanel
            implements javax.swing.table.TableCellRenderer {
        private final JLabel editAction = createActionLabel("Sửa", new Color(229, 241, 255),
                new Color(24, 105, 194));
        private final JLabel deleteAction = createActionLabel("Xóa", new Color(255, 232, 234),
                new Color(194, 50, 62));

        CustomerActionsRenderer() {
            super(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
            add(editAction);
            add(deleteAction);
        }

        private static JLabel createActionLabel(String text, Color background, Color foreground) {
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBackground(background);
            label.setForeground(foreground);
            label.setFont(new Font("SansSerif", Font.BOLD, 11));
            label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            return label;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int column) {
            setBackground(selected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(java.awt.LayoutManager layout, int radius) {
            super(layout);
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    private static final class RoundedBorder implements javax.swing.border.Border {
        private final int radius;
        private final Color color;
        private final int thickness;

        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + 1, y + 1, width - 2, height - 2, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private void createCustomer() {
        String fullName = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        Customer.CustomerType type = (Customer.CustomerType) customerTypeBox.getSelectedItem();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin khách hàng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            service.addCustomer(fullName, phone, email, type);
            JOptionPane.showMessageDialog(this, "Khách hàng đã được tạo thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            nameField.setText("");
            phoneField.setText("");
            emailField.setText("");
        } catch (IllegalArgumentException | java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}