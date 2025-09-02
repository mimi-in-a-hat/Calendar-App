import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class to create single events.
 */
public class SingleEventBuilder {

  /**
   * Creates a new single event.
   *
   * @param subject     of the event
   * @param start       time of the event
   * @param end         time of the event
   * @param description of the event
   * @param location    of the event
   * @param status      (public or private) event
   * @return newly created Event object
   */
  public Event createEvent(String subject, LocalDateTime start,
                           LocalDateTime end, String description,
                           String location, String status) {
    UUID id = UUID.randomUUID();

    Event event = new Event.EventBuilder()
            .subject(subject)
            .start(start)
            .end(end)
            .description(description)
            .location(location)
            .status(status)
            .identifier(id)
            .build();

    return event;
  }


  /**
   * Edit the properties of one event.
   * If time is changed, updates the UUID.
   *
   * @param event    to be changed
   * @param property to be changed about the event
   * @param newValue of the changed property
   */
  public Event editEvent(Event event, String property, String newValue) {
    Event oldEvent = event;
    Event updated = updateEventProperty(oldEvent, property, newValue);


    if (property.equals("start") || property.equals("end")) {
      Event newUUID = new Event(updated.getSubject(), updated.getStart(),
              updated.getDescription(), updated.getEnd(),
              updated.getLocation(), updated.getStatus(), UUID.randomUUID());
      return newUUID;
    } else {
      return updated;
    }
  }

  //update property
  protected Event updateEventProperty(Event old, String property, String value) {
    Event.EventBuilder builder = new Event.EventBuilder()
            .subject(old.getSubject())
            .start(old.getStart())
            .end(old.getEnd())
            .description(old.getDescription())
            .location(old.getLocation())
            .status(old.getStatus())
            .identifier(old.getIdentifier());

    if ("subject".equalsIgnoreCase(property)) {
      builder.subject(value);
    } else if ("description".equalsIgnoreCase(property)) {
      builder.description(value);
    } else if ("location".equalsIgnoreCase(property)) {
      builder.location(value);
    } else if ("status".equalsIgnoreCase(property)) {
      builder.status(value);
    } else if ("start".equalsIgnoreCase(property)) {
      builder.start(LocalDateTime.parse(value));
    } else if ("end".equalsIgnoreCase(property)) {
      builder.end(LocalDateTime.parse(value));
    } else {
      throw new IllegalArgumentException("Unknown property: " + property);
    }

    return builder.build();
  }

}
