import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for GUI version of calendar.
 */
public class CalendarAppView extends JFrame {
  private CalendarManagement calendar;
  private JTable eventTable;
  private JSpinner dateSpinner;
  private DefaultTableModel tableModel;
  private static final DateTimeFormatter formatter =
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  /**
   * Setup for initializing the calendar app.
   *
   * @param calendar to be used
   */
  public CalendarAppView(CalendarManagement calendar) {
    super("Calendar App GUI");
    this.calendar = calendar;

    setLayout(new BorderLayout());
    initUI();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 400);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void initUI() {
    JPanel topPanel = new JPanel();

    dateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner,
            "yyyy-MM-dd");
    dateSpinner.setEditor(editor);

    JButton refreshButton = new JButton("Refresh List");
    JButton addButton = new JButton("Add Event");
    JButton editButton = new JButton("Edit Selected Event");

    topPanel.add(new JLabel("Start Date:"));
    topPanel.add(dateSpinner);
    topPanel.add(refreshButton);
    topPanel.add(addButton);
    topPanel.add(editButton);

    add(topPanel, BorderLayout.NORTH);

    tableModel = new DefaultTableModel(
            new Object[]{"Subject", "Start", "End", "Description"}, 0);
    eventTable = new JTable(tableModel);
    add(new JScrollPane(eventTable), BorderLayout.CENTER);

    refreshButton.addActionListener(e -> loadEvents());
    addButton.addActionListener(e -> new AddEventDialog(this, calendar));
    editButton.addActionListener(e -> editSelectedEvent());
  }

  private void loadEvents() {
    tableModel.setRowCount(0);
    Date date = (Date) dateSpinner.getValue();
    LocalDate selectedDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    List<Event> events = calendar.getAllEvents().stream()
            .filter(e -> !e.getStart().toLocalDate().isBefore(selectedDate))
            .sorted((e1, e2) -> e1.getStart().compareTo(e2.getStart()))
            .limit(10)
            .collect(Collectors.toList());

    for (Event e : events) {
      tableModel.addRow(new Object[]{
              e.getSubject(),
              formatter.format(e.getStart()),
              formatter.format(e.getEnd()),
              e.getDescription() != null ? e.getDescription() : ""
      });
    }
  }

  private void editSelectedEvent() {
    int selectedRow = eventTable.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(this,
              "Please select an event to edit.");
      return;
    }

    String subject = (String) tableModel.getValueAt(selectedRow, 0);
    LocalDateTime start = LocalDateTime.parse(
            (String) tableModel.getValueAt(selectedRow, 1), formatter);

    Event selectedEvent = calendar.getAllEvents().stream()
            .filter(e -> e.getSubject().equals(subject) && e.getStart().equals(start))
            .findFirst().orElse(null);

    if (selectedEvent == null) {
      JOptionPane.showMessageDialog(this,
              "Selected event could not be found.");
      return;
    }

    new EditEventDialog(this, calendar, selectedEvent);
    loadEvents();
  }
}

/**
 * Create events.
 */
class AddEventDialog extends JDialog {
  /**
   * Constructor for creating events
   *
   * @param parent   JFrame to be utilized for this operation
   * @param calendar to add event to
   */
  public AddEventDialog(JFrame parent, CalendarManagement calendar) {
    super(parent, "Add Event", true);
    setLayout(new GridLayout(6, 2));

    JTextField subjectField = new JTextField();
    JTextField descriptionField = new JTextField();
    JTextField locationField = new JTextField();

    JSpinner startSpinner = new JSpinner(new SpinnerDateModel());
    startSpinner.setEditor(new JSpinner.DateEditor(startSpinner,
            "yyyy-MM-dd HH:mm"));

    JSpinner endSpinner = new JSpinner(new SpinnerDateModel());
    endSpinner.setEditor(new JSpinner.DateEditor(endSpinner,
            "yyyy-MM-dd HH:mm"));

    JButton addButton = new JButton("Create");

    add(new JLabel("Subject:"));
    add(subjectField);
    add(new JLabel("Description:"));
    add(descriptionField);
    add(new JLabel("Location:"));
    add(locationField);
    add(new JLabel("Start Time:"));
    add(startSpinner);
    add(new JLabel("End Time:"));
    add(endSpinner);
    add(new JLabel());
    add(addButton);

    addButton.addActionListener(e -> {
      String subject = subjectField.getText().trim();
      if (subject.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Subject cannot be empty.");
        return;
      }

      LocalDateTime start = ((Date) startSpinner.getValue()).toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDateTime();
      LocalDateTime end = ((Date) endSpinner.getValue()).toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDateTime();

      if (!start.isBefore(end)) {
        JOptionPane.showMessageDialog(this,
                "Start time must be before end time.");
        return;
      }

      try {
        calendar.addSingleEvent(subject, start, end,
                descriptionField.getText().trim(),
                locationField.getText().trim(),
                null);
        JOptionPane.showMessageDialog(this,
                "Event added successfully.");
        dispose();
      } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this,
                "An identical event already exists.\n" +
                        "Please change the subject or time to avoid duplication.",
                "Duplicate Event",
                JOptionPane.WARNING_MESSAGE);
      }
    });

    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }
}

/**
 * Editor to edit events after creation.
 */
class EditEventDialog extends JDialog {
  /**
   * Constructor for the part of the GUI that handles event editing.
   *
   * @param parent   parent JFrame to use
   * @param calendar to access
   * @param event    to be edited
   */
  public EditEventDialog(JFrame parent, CalendarManagement calendar, Event event) {
    super(parent, "Edit Event", true);
    setLayout(new GridLayout(6, 2));

    JTextField subjectField = new JTextField(event.getSubject());
    JTextField descriptionField = new JTextField(event.getDescription());
    JTextField locationField = new JTextField(event.getLocation());

    JSpinner startSpinner = new JSpinner(new SpinnerDateModel());
    startSpinner.setEditor(new JSpinner.DateEditor(startSpinner,
            "yyyy-MM-dd HH:mm"));
    startSpinner.setValue(
            Date.from(event.getStart().atZone(ZoneId.systemDefault()).toInstant()));

    JSpinner endSpinner = new JSpinner(new SpinnerDateModel());
    endSpinner.setEditor(new JSpinner.DateEditor(endSpinner,
            "yyyy-MM-dd HH:mm"));
    endSpinner.setValue(
            Date.from(event.getEnd().atZone(ZoneId.systemDefault()).toInstant()));

    JButton saveButton = new JButton("Save");

    add(new JLabel("Subject:"));
    add(subjectField);
    add(new JLabel("Description:"));
    add(descriptionField);
    add(new JLabel("Location:"));
    add(locationField);
    add(new JLabel("Start Time:"));
    add(startSpinner);
    add(new JLabel("End Time:"));
    add(endSpinner);
    add(new JLabel());
    add(saveButton);

    saveButton.addActionListener(e -> {
      LocalDateTime newStart = ((Date) startSpinner.getValue()).toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDateTime();
      LocalDateTime newEnd = ((Date) endSpinner.getValue()).toInstant()
              .atZone(ZoneId.systemDefault()).toLocalDateTime();

      if (!newStart.isBefore(newEnd)) {
        JOptionPane.showMessageDialog(this,
                "Start time must be before end time.");
        return;
      }

      calendar.editSingleEvent(event, "subject", subjectField.getText().trim());
      calendar.editSingleEvent(event, "start", newStart.toString());
      calendar.editSingleEvent(event, "end", newEnd.toString());
      calendar.editSingleEvent(event, "description", descriptionField.getText().trim());
      calendar.editSingleEvent(event, "location", locationField.getText().trim());

      JOptionPane.showMessageDialog(this, "Event updated successfully.");
      dispose();
    });

    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }
}
