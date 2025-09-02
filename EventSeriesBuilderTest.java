import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for EventSeriesBuilder (which builds off of SingleEventBuilder).
 */
public class EventSeriesBuilderTest {

  private EventSeriesBuilder builder;
  private LocalDate baseDate;

  @Before
  public void setUp() {
    builder = new EventSeriesBuilder();
    baseDate = LocalDate.of(2025, 6, 2); // Monday
  }

  @Test
  public void testCreateSeriesByOccurrences() {
    List<Event> events = builder.createSeriesByOccurrences(
            "Study",
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            baseDate,
            "MWF",
            5,
            "Morning session",
            "Library",
            "private"
    );

    assertEquals(5, events.size());
    for (Event e : events) {
      assertEquals("Study", e.getSubject());
      assertEquals("Library", e.getLocation());
    }
  }

  @Test
  public void testCreateSeriesUntilDate() {
    List<Event> events = builder.createSeriesUntilDate(
            "Yoga",
            LocalTime.of(7, 0),
            LocalTime.of(8, 0),
            baseDate,
            baseDate.plusDays(6),
            "MTWRF",
            "Morning yoga",
            "Park",
            "public"
    );

    assertEquals(5, events.size()); // M-F
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeSpan() {
    builder.createSeriesByOccurrences(
            "Error",
            LocalTime.of(15, 0),
            LocalTime.of(14, 0),
            baseDate,
            "M",
            1,
            "",
            "",
            "public"
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWeekdayCode() {
    builder.createSeriesByOccurrences(
            "Error",
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            baseDate,
            "MX", // 'X' is invalid
            1,
            "",
            "",
            "public"
    );
  }

  @Test
  public void testEditEventAndFutureSeries() {
    List<Event> events = builder.createSeriesByOccurrences(
            "Lecture",
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            baseDate,
            "MW",
            4,
            "CS class",
            "Room 1",
            "public"
    );

    LocalDateTime editStart = baseDate.plusDays(2).atTime(10, 0); // Wednesday
    builder.editEventAndFutureSeries(events, "Lecture", editStart, "location", "Room 2");

    for (Event e : events) {
      if (!e.getStart().isBefore(editStart)) {
        assertEquals("Room 2", e.getLocation());
      } else {
        assertEquals("Room 1", e.getLocation());
      }
    }

    long oldSeries = events.stream().filter(
        e -> e.getLocation().equals("Room 1")).map(
                Event::getIdentifier).distinct().count();
    long newSeries = events.stream().filter(
        e -> e.getLocation().equals("Room 2")).map(
                Event::getIdentifier).distinct().count();
    assertTrue(newSeries <= 1);
    assertTrue(oldSeries <= 1);
  }

  @Test
  public void testEditEntireSeries() {
    List<Event> events = builder.createSeriesByOccurrences(
            "Group Work",
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            baseDate,
            "TR",
            2,
            "Initial",
            "Room A",
            "private"
    );

    LocalDateTime firstStart = events.get(0).getStart();
    List<Event> updated = builder.editEntireSeries(
            events, "Group Work", firstStart, "description",
                    "Updated");

    for (Event e : updated) {
      assertEquals("Updated", e.getDescription());
    }
  }

  @Test
  public void testEditEntireSeriesWhenNotInSeries() {
    Event single = new Event.EventBuilder()
            .subject("Solo")
            .start(baseDate.atTime(16, 0))
            .end(baseDate.atTime(17, 0))
            .description("One-off")
            .location("Cafe")
            .status("private")
            .build();

    List<Event> list = new ArrayList<>();
    list.add(single);

    List<Event> updated = builder.editEntireSeries(list,
            "Solo", single.getStart(), "location", "Changed");

    assertEquals(1, updated.size());
    assertEquals("Changed", updated.get(0).getLocation());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventAndFutureSeriesThrowsIfNotFound() {
    List<Event> list = new ArrayList<>();
    builder.editEventAndFutureSeries(list, "Missing",
            baseDate.atTime(8, 0), "location", "Nowhere");
  }
}