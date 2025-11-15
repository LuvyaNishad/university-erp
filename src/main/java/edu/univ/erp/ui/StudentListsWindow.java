package edu.univ.erp.ui;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A window for instructors to view students enrolled in their sections.
 * This is for the "Student Lists" button.
 */
public class StudentListsWindow extends JDialog {

    private JComboBox<SectionItem> sectionComboBox;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private InstructorService instructorService;
    private List<Section> sectionList;

    public StudentListsWindow(JFrame owner) {
        super(owner, "View Student Lists", true);
        this.instructorService = new InstructorService();

        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadSections();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        // --- Top Panel (Selector) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JLabel selectLabel = new JLabel("Select a Section:");
        UITheme.styleLabel(selectLabel);
        sectionComboBox = new JComboBox<>();
        sectionComboBox.setPreferredSize(new Dimension(350, 30));
        topPanel.add(selectLabel);
        topPanel.add(sectionComboBox);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"Student ID", "Roll No & Program", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);
        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        sectionComboBox.addActionListener(e -> loadStudents());

        add(mainPanel);
    }

    /**
     * Loads the instructor's sections into the combo box.
     */
    private void loadSections() {
        try {
            String instructorId = AuthService.getCurrentUserId();
            this.sectionList = instructorService.getMySections(instructorId);

            sectionComboBox.removeAllItems();
            sectionComboBox.addItem(new SectionItem(null, "--- Select a section ---"));
            for (Section s : sectionList) {
                sectionComboBox.addItem(new SectionItem(s, s.getCourseTitle() + " (" + s.getSectionId() + ")"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading sections: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads students for the selected section.
     */
    private void loadStudents() {
        SectionItem selectedItem = (SectionItem) sectionComboBox.getSelectedItem();
        if (selectedItem == null || selectedItem.section == null) {
            tableModel.setRowCount(0); // Clear table if "select" is chosen
            return;
        }

        try {
            String instructorId = AuthService.getCurrentUserId();
            String sectionId = selectedItem.section.getSectionId();

            // This service call is already secured
            List<Enrollment> enrollments = instructorService.getSectionEnrollments(sectionId, instructorId);

            tableModel.setRowCount(0); // Clear table
            for (Enrollment en : enrollments) {
                tableModel.addRow(new Object[]{
                        en.getStudentId(),
                        en.getStudentName(), // This is set by EnrollmentDAO
                        en.getStatus().toUpperCase()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class to store Section object in JComboBox
    private static class SectionItem {
        Section section;
        String displayValue;

        SectionItem(Section section, String displayValue) {
            this.section = section;
            this.displayValue = displayValue;
        }

        @Override
        public String toString() {
            return displayValue;
        }
    }
}