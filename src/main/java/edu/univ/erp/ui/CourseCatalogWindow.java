package edu.univ.erp.ui;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A window for students to browse and register for courses.
 */
public class CourseCatalogWindow extends JDialog {

    private JTable courseTable;
    private DefaultTableModel tableModel;
    private StudentService studentService;
    private List<Section> sectionList;
    private JLabel statusLabel;

    public CourseCatalogWindow(JFrame owner) {
        super(owner, "Course Catalog & Registration", true);
        this.studentService = new StudentService();

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadCourseData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Course Catalog");
        UITheme.styleSubHeaderLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Section ID", "Course", "Title", "Credits", "Instructor", "Schedule", "Room", "Seats (Filled/Total)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        statusLabel = new JLabel("Select a section to register.");
        statusLabel.setFont(UITheme.FONT_BODY);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton registerButton = new JButton("Register for Selected Section");
        UITheme.stylePrimaryButton(registerButton);
        registerButton.setPreferredSize(new Dimension(250, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(UITheme.COLOR_BACKGROUND);
        buttonContainer.add(registerButton);
        buttonContainer.add(closeButton);

        bottomPanel.add(buttonContainer, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        registerButton.addActionListener(e -> handleRegister());

        add(mainPanel);
    }

    /**
     * Loads all available sections from the database into the table.
     */
    private void loadCourseData() {
        try {
            // This service method gets all sections and their details
            this.sectionList = studentService.getAvailableSections();

            // Clear existing data
            tableModel.setRowCount(0);

            for (Section s : sectionList) {
                // The service layer populates current enrollment in getCapacity()
                // and total capacity in getYear() (a temporary fix from your service)
                // Let's assume StudentService.getAvailableSections() is fixed
                // to populate s.getCurrentEnrollment() and s.getCapacity() correctly.

                // We'll call the DAO directly for a clean implementation
                int currentEnrollment = edu.univ.erp.data.SectionDAO.getCurrentEnrollment(s.getSectionId());
                int totalCapacity = s.getCapacity(); // Get the real capacity
                String seats = currentEnrollment + " / " + totalCapacity;

                tableModel.addRow(new Object[]{
                        s.getSectionId(),
                        s.getCourseId(), // Using courseId as Course Code
                        s.getCourseTitle(),
                        s.getCredits(),
                        s.getInstructorName(), // Assumes SectionDAO joins this
                        s.getDayTime(),
                        s.getRoom(),
                        seats
                });
            }
            statusLabel.setText(sectionList.size() + " sections found.");
        } catch (Exception e) {
            statusLabel.setText("Error loading courses.");
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the register button click.
     */
    private void handleRegister() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section from the table first.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String studentId = AuthService.getCurrentUserId();
            // Get the Section object from our list, not just the table
            Section selectedSection = sectionList.get(selectedRow);
            String sectionId = selectedSection.getSectionId();

            // This service call handles all logic (full, duplicate, etc.)
            boolean success = studentService.registerForSection(studentId, sectionId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Successfully registered for " + selectedSection.getCourseTitle() + "!", "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData(); // Refresh table to show new seat count
            } else {
                showError("Registration failed for an unknown reason.");
            }

        } catch (Exception e) {
            // Catch errors from the service layer (e.g., "Section is full", "Already enrolled")
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}