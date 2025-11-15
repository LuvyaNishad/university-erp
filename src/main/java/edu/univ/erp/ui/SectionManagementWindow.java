package edu.univ.erp.ui;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A window for Admins to create new sections and assign instructors.
 * This passes the test: "[ ] ...and a section; assign instructor."
 */
public class SectionManagementWindow extends JDialog {

    private AdminService adminService;
    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private JTextField sectionIdField, dayTimeField, roomField, capacityField, semesterField, yearField;
    private JComboBox<CourseItem> courseComboBox;
    private JComboBox<InstructorItem> instructorComboBox;

    private List<Course> courseList;
    private List<Instructor> instructorList;

    public SectionManagementWindow(JFrame owner) {
        super(owner, "Section Management", true);
        this.adminService = new AdminService();

        setSize(900, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UITheme.BORDER_PADDING_DIALOG);
        mainPanel.setBackground(UITheme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Manage Sections");
        UITheme.styleSubHeaderLabel(titleLabel);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Center: Table of existing sections ---
        String[] columnNames = {"Section ID", "Course", "Instructor", "Schedule", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sectionsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        UITheme.styleTable(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom: Form for new section ---
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        formPanel.setBackground(UITheme.COLOR_WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Section"));

        formPanel.add(new JLabel("Section ID (e.g., 'SEC003'):"));
        sectionIdField = new JTextField();
        UITheme.styleTextField(sectionIdField);
        formPanel.add(sectionIdField);

        formPanel.add(new JLabel("Select Course:"));
        courseComboBox = new JComboBox<>();
        formPanel.add(courseComboBox);

        formPanel.add(new JLabel("Select Instructor:"));
        instructorComboBox = new JComboBox<>();
        formPanel.add(instructorComboBox);

        formPanel.add(new JLabel("Schedule (e.g., 'Mon 1-2:30'):"));
        dayTimeField = new JTextField();
        UITheme.styleTextField(dayTimeField);
        formPanel.add(dayTimeField);

        formPanel.add(new JLabel("Room (e.g., 'R103'):"));
        roomField = new JTextField();
        UITheme.styleTextField(roomField);
        formPanel.add(roomField);

        formPanel.add(new JLabel("Capacity (e.g., 30):"));
        capacityField = new JTextField();
        UITheme.styleTextField(capacityField);
        formPanel.add(capacityField);

        formPanel.add(new JLabel("Semester (e.g., 'Fall'):"));
        semesterField = new JTextField();
        UITheme.styleTextField(semesterField);
        formPanel.add(semesterField);

        formPanel.add(new JLabel("Year (e.g., 2024):"));
        yearField = new JTextField();
        UITheme.styleTextField(yearField);
        formPanel.add(yearField);

        mainPanel.add(formPanel, BorderLayout.SOUTH);

        // --- Button Panel (Right) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(UITheme.COLOR_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JButton createButton = new JButton("Create Section");
        UITheme.stylePrimaryButton(createButton);
        createButton.setMinimumSize(new Dimension(150, 40));
        createButton.setMaximumSize(new Dimension(150, 40));

        JButton closeButton = new JButton("Close");
        UITheme.styleSecondaryButton(closeButton);
        closeButton.setMinimumSize(new Dimension(150, 40));
        closeButton.setMaximumSize(new Dimension(150, 40));

        buttonPanel.add(createButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        // --- Action Listeners ---
        closeButton.addActionListener(e -> dispose());
        createButton.addActionListener(e -> handleCreateSection());

        add(mainPanel);
    }

    private void loadData() {
        try {
            // Load sections for table
            List<Section> sections = adminService.getAllSections();
            tableModel.setRowCount(0);
            for (Section s : sections) {
                tableModel.addRow(new Object[]{
                        s.getSectionId(),
                        s.getCourseTitle(), // Assumes SectionDAO joins this
                        s.getInstructorName(), // Assumes SectionDAO joins this
                        s.getDayTime(),
                        s.getCapacity()
                });
            }

            // Load courses for combo box
            this.courseList = adminService.getAllCourses();
            courseComboBox.removeAllItems();
            courseComboBox.addItem(new CourseItem(null, "--- Select Course ---"));
            for (Course c : courseList) {
                courseComboBox.addItem(new CourseItem(c, c.getTitle()));
            }

            // Load instructors for combo box
            this.instructorList = adminService.getAllInstructors();
            instructorComboBox.removeAllItems();
            instructorComboBox.addItem(new InstructorItem(null, "--- Select Instructor ---"));
            for (Instructor i : instructorList) {
                // Assuming Instructor.toString() is reasonable, or use a custom renderer
                instructorComboBox.addItem(new InstructorItem(i, i.getUserId() + " (" + i.getDepartment() + ")"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateSection() {
        try {
            Section section = new Section();
            section.setSectionId(sectionIdField.getText().trim());
            section.setDayTime(dayTimeField.getText().trim());
            section.setRoom(roomField.getText().trim());
            section.setSemester(semesterField.getText().trim());
            section.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            section.setYear(Integer.parseInt(yearField.getText().trim()));

            // Get selected course and instructor
            CourseItem selectedCourse = (CourseItem) courseComboBox.getSelectedItem();
            InstructorItem selectedInstructor = (InstructorItem) instructorComboBox.getSelectedItem();

            if (selectedCourse == null || selectedCourse.course == null ||
                    selectedInstructor == null || selectedInstructor.instructor == null ||
                    section.getSectionId().isEmpty()) {
                showError("Section ID, Course, and Instructor are required.");
                return;
            }

            section.setCourseId(selectedCourse.course.getCourseId());
            section.setInstructorId(selectedInstructor.instructor.getUserId());

            boolean success = adminService.createSection(section);
            if (success) {
                JOptionPane.showMessageDialog(this, "Section created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); // Refresh table
                // Clear fields
                sectionIdField.setText("");
                dayTimeField.setText("");
                roomField.setText("");
                capacityField.setText("");
                semesterField.setText("");
                yearField.setText("");
            } else {
                showError("Failed to create section. Check if Section ID already exists.");
            }
        } catch (NumberFormatException e) {
            showError("Capacity and Year must be valid numbers.");
        } catch (Exception e) {
            showError("Error creating section: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Helper classes for ComboBox
    private static class CourseItem {
        Course course;
        String displayValue;
        CourseItem(Course course, String displayValue) { this.course = course; this.displayValue = displayValue; }
        @Override public String toString() { return displayValue; }
    }

    private static class InstructorItem {
        Instructor instructor;
        String displayValue;
        InstructorItem(Instructor instructor, String displayValue) { this.instructor = instructor; this.displayValue = displayValue; }
        @Override public String toString() { return displayValue; }
    }
}