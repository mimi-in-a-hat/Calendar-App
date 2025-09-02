import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test class for CalendarManagement.
 */
public class CalendarManagementTest {

  private CalendarManagement calendar;
  private LocalDate baseDate;

  @Before
  public void setUp() {
    calendar = new CalendarManagement();
    baseDate = LocalDate.of(2025, 6, 2);
  }

  @Test
  public void testAddSingleEvent() {
    calendar.addSingleEvent(
            "Dentist",
            baseDate.atTime(9, 0),
            baseDate.atTime(10, 0),
            "Teeth cleaning and x-ray",
            "Dental Office",
            "private"
    );

    List<Event> all = calendar.getAllEvents();
    assertEquals(1, all.size());
    assertEquals("Dentist", all.get(0).getSubject());
  }

  @Test
  public void testCreateSeriesByOccurrences() {
    calendar.addEventSeriesByOccurrences(
            "Workout",
            LocalTime.of(7, 0),
            LocalTime.of(8, 0),
            baseDate,
            "MWF",
            3,
            "Morning gym",
            "Lifetime Fitness Burlington",
            "public"
    );

    assertEquals(3, calendar.getAllEvents().size());
  }

  @Test
  public void testCreateSeriesUntilDate() {
    calendar.addEventSeriesUntilDate(
            "Yoga",
            LocalTime.of(6, 0),
            LocalTime.of(7, 0),
            baseDate,
            baseDate.plusDays(7),
            "MTWTF",
            "Yoga!!!",
            "Park",
            "public"
    );

    assertEquals(5, calendar.getAllEvents().size());
  }

  @Test
  public void testEditEventAndFutureSeries() {
    calendar.addEventSeriesByOccurrences(
            "Class",
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            baseDate,
            "MW",
            4,
            "Lecture",
            "Room 101",
            "public"
    );

    LocalDateTime editStart = baseDate.atTime(10, 0);
    calendar.editSeriesFromDate("Class", editStart,
            "location", "Room 202");

    List<Event> events = calendar.getAllEvents();
    for (Event e : events) {
      if (!e.getStart().isBefore(editStart)) {
        assertEquals("Room 202", e.getLocation());
      }
    }
  }

  @Test
  public void testEditEntireSeries() {
    calendar.addEventSeriesByOccurrences(
            "Class",
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            baseDate,
            "MW",
            4,
            "Lecture",
            "Room 101",
            "public"
    );

    LocalDateTime startTime = baseDate.atTime(10, 0);
    calendar.editEntireSeries("Class", startTime,
            "location", "Room 102");

    for (Event e : calendar.getAllEvents()) {
      assertEquals("Room 102", e.getLocation());
    }
  }

  @Test
  public void testGetEventsBetween() {
    calendar.addSingleEvent("Meeting A",
            baseDate.atTime(9, 0), baseDate.atTime(10, 0), "", "", "public");
    calendar.addSingleEvent("Meeting B",
            baseDate.atTime(11, 0), baseDate.atTime(12, 0), "", "", "public");

    List<Event> results = calendar.getEventsBetween(baseDate.atTime(8, 0), baseDate.atTime(11, 0));

    assertEquals(2, results.size());
  }

  @Test
  public void testIsTimeSlotOccupied() {
    calendar.addSingleEvent("testEvent", baseDate.atTime(14, 0),
            baseDate.atTime(15, 0),
            "", "", "public");

    assertTrue(calendar.isTimeSlotOccupied(baseDate.atTime(14, 30)));
    assertFalse(calendar.isTimeSlotOccupied(baseDate.atTime(13, 59)));
    assertFalse(calendar.isTimeSlotOccupied(baseDate.atTime(15, 0)));  // end is exclusive
  }
}