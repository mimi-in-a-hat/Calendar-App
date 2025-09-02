import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for SingleEventBuilder.
 */
public class CalendarBuilderTest {

  private SingleEventBuilder builder;
  private Event baseEvent;

  @Before
  public void setup() {
    builder = new SingleEventBuilder();
  }

  @Test
  public void testCreateSingleEvent() {
    Event event = builder.createEvent("Meeting",
            LocalDateTime.of(2025, 6, 3, 10, 0),
            LocalDateTime.of(2025, 6, 3, 11, 0),
            "Project discussion", "Room 101", "public");

    assertEquals("Meeting", event.getSubject());
    assertEquals("Project discussion", event.getDescription());
    assertEquals("Room 101", event.getLocation());
    assertEquals("public", event.getStatus());
    assertNotNull(event.getIdentifier());
  }

  @Before
  public void setUp() {
    builder = new SingleEventBuilder();
    baseEvent = builder.createEvent("Workshop",
            LocalDateTime.of(2025, 6, 10, 10, 0),
            LocalDateTime.of(2025, 6, 10, 12, 0),
            "Tech workshop",
            "Conference Room A",
            "public");
  }

  @Test
  public void testEditSubject() {
    Event edited = builder.editEvent(baseEvent, "subject", "Seminar");

    assertEquals("Seminar", edited.getSubject());
    assertEquals(baseEvent.getDescription(), edited.getDescription());
  }

  @Test
  public void testEditDescription() {
    Event edited = builder.editEvent(baseEvent, "description", "Updated workshop details");

    assertEquals("Updated workshop details", edited.getDescription());
    assertEquals(baseEvent.getSubject(), edited.getSubject());
  }

  @Test
  public void testEditLocation() {
    Event edited = builder.editEvent(baseEvent, "location", "Room B");

    assertEquals("Room B", edited.getLocation());
    assertEquals(baseEvent.getStart(), edited.getStart());
  }

  @Test
  public void testEditStatus() {
    Event edited = builder.editEvent(baseEvent, "status", "private");

    assertEquals("private", edited.getStatus());
    assertEquals(baseEvent.getEnd(), edited.getEnd());
  }

  @Test
  public void testEditStartTime() {
    String newStart = "2025-06-10T11:00";
    Event edited = builder.editEvent(baseEvent, "start", newStart);

    assertEquals(LocalDateTime.parse(newStart), edited.getStart());
    assertEquals(baseEvent.getSubject(), edited.getSubject());
  }

  @Test
  public void testEditEndTime() {
    String newEnd = "2025-06-10T13:00";
    Event edited = builder.editEvent(baseEvent, "end", newEnd);

    assertEquals(LocalDateTime.parse(newEnd), edited.getEnd());
    assertEquals(baseEvent.getLocation(), edited.getLocation());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidProperty() {
    Event original = builder.createEvent("Review",
            LocalDateTime.of(2025, 6, 4, 14, 0),
            LocalDateTime.of(2025, 6, 4, 15, 0),
            "desc", "Room 202", "private");

    builder.editEvent(original, "invalidProp", "value");
  }
}