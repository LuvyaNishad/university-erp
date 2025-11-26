package edu.univ.erp.ui;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class CourseCatalogWindow extends JDialog {

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private List<Section> sectionList;
    private JLabel statusLabel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public CourseCatalogWindow(JFrame owner) {
        super(owner, "Course Catalog & Registration", true);
        this.studentService = new StudentService();

        setSize(1000, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadCourseData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        // --- Top Panel: Title + Search ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Course Catalog");
        UITheme.styleSubHeaderLabel(titleLabel);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // FIX: Added Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(UITheme.COLOR_BACKGROUND);
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        UITheme.styleTextField(searchField);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        searchPanel.add(searchField);
        topPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Section ID", "Course", "Title", "Credits", "Instructor", "Schedule", "Room", "Seats"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            // Fix sorting for integer columns
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class; // Credits
                return String.class;
            }
        };

        courseTable = new JTable(tableModel);

        // FIX: Enable Sorting
        sorter = new TableRowSorter<>(tableModel);
        courseTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(courseTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        statusLabel = new JLabel("Select a section to register.");
        statusLabel.setFont(UITheme.FONT_BODY);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton registerButton = new JButton("Register");
        UITheme.stylePrimaryButton(registerButton);
        registerButton.setPreferredSize(new Dimension(150, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(UITheme.COLOR_BACKGROUND);
        buttonContainer.add(registerButton);
        buttonContainer.add(closeButton);

        bottomPanel.add(buttonContainer, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> dispose());
        registerButton.addActionListener(e -> handleRegister());

        add(mainPanel);
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void loadCourseData() {
        try {
            this.sectionList = studentService.getAvailableSections();
            tableModel.setRowCount(0);

            for (Section s : sectionList) {
                int currentEnrollment = edu.univ.erp.data.SectionDAO.getCurrentEnrollment(s.getSectionId());
                int totalCapacity = s.getCapacity();
                String seats = currentEnrollment + " / " + totalCapacity;

                tableModel.addRow(new Object[]{
                        s.getSectionId(),
                        s.getCourseId(),
                        s.getCourseTitle(),
                        s.getCredits(),
                        s.getInstructorName(),
                        s.getDayTime(),
                        s.getRoom(),
                        seats
                });
            }
            statusLabel.setText(sectionList.size() + " sections found.");
        } catch (Exception e) {
            statusLabel.setText("Error loading courses.");
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        int viewRow = courseTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view index to model index due to sorting
        int modelRow = courseTable.convertRowIndexToModel(viewRow);
        String sectionId = (String) tableModel.getValueAt(modelRow, 0); // Get ID from table, safer than list index with sorting

        try {
            String studentId = AuthService.getCurrentUserId();
            // Find object for title display
            Section selected = sectionList.stream().filter(s -> s.getSectionId().equals(sectionId)).findFirst().orElse(null);

            boolean success = studentService.registerForSection(studentId, sectionId);

            if (success) {
                String title = (selected != null) ? selected.getCourseTitle() : "Course";
                JOptionPane.showMessageDialog(this, "Successfully registered for " + title + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData();
            } else {
                showError("Registration failed.");
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}