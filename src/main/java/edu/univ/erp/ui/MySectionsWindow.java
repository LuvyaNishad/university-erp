package edu.univ.erp.ui;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A window for instructors to view all sections assigned to them.
 * This passes the test: "[ ] See only their own sections."
 */
public class MySectionsWindow extends JDialog {

    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;
    private JLabel statusLabel;

    public MySectionsWindow(JFrame owner) {
        super(owner, "My Assigned Sections", true);
        this.instructorService = new InstructorService();

        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadSections();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("My Assigned Sections");
        UITheme.styleSubHeaderLabel(titleLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Section ID", "Course", "Schedule", "Room", "Seats (Filled/Total)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        sectionsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Status and Close Button ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(UITheme.COLOR_BACKGROUND);

        statusLabel = new JLabel("Loading sections...");
        statusLabel.setFont(UITheme.FONT_BODY);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(UITheme.COLOR_BACKGROUND);
        buttonContainer.add(closeButton);

        bottomPanel.add(buttonContainer, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Loads the sections for the currently logged-in instructor.
     */
    private void loadSections() {
        try {
            String instructorId = AuthService.getCurrentUserId();
            // This service call is already secured by AccessControl
            List<Section> sections = instructorService.getMySections(instructorId);

            // Clear existing data
            tableModel.setRowCount(0);

            if (sections.isEmpty()) {
                statusLabel.setText("You are not currently assigned to any sections.");
            } else {
                for (Section s : sections) {
                    // Get up-to-date enrollment count
                    int currentEnrollment = instructorService.getSectionEnrollments(s.getSectionId(), instructorId).size();
                    String seats = currentEnrollment + " / " + s.getCapacity();

                    tableModel.addRow(new Object[]{
                            s.getSectionId(),
                            s.getCourseTitle() + " (" + s.getCourseId() + ")",
                            s.getDayTime(),
                            s.getRoom(),
                            seats
                    });
                }
                statusLabel.setText(sections.size() + " section(s) found.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading sections.");
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}