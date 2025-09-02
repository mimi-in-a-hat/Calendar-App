import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class to represent a singular event or series of events.
 * Every event must include a subject and a start date/time.
 * Optionally, an event may also include a longer description, an end date/time,
 * a location (physical or online), and a status (public/private).
 * An event can also span multiple days.
 * This class uses the builder pattern design so any specific part of an
 * event can be updated one-by-one.
 */
public class Event {

  //MAKE PRIVATE
  private String subject;
  private LocalDateTime start;

  private String description;
  private LocalDateTime end;
  private String location;
  private String status;


  //Identifier used for event series. Events in a series will share same
  //identifier; events that do not will have unique UUIDs.
  private UUID identifier;


  /**
   * Constructor for a single Event object.
   *
   * @param subject     of event
   * @param start       time of event
   * @param description of event
   * @param end         time of event
   * @param location    of event
   * @param status      of event; public or private
   */
  public Event(String subject, LocalDateTime start, String description,
               LocalDateTime end, String location, String status, UUID identifier) {
    this.subject = subject;
    this.start = start;
    this.description = description;
    this.end = end;
    this.location = location;
    this.status = status;
    this.identifier = identifier;
  }


  /**
   * Builder class for the Event object.
   */
  public static class EventBuilder {
    private String subject;
    private LocalDateTime start;
    private String description;
    private LocalDateTime end;
    private String location;
    private String status;
    private UUID identifier;

    /**
     * Basic constructor for the EventBuilder object with default values.
     */
    public EventBuilder() {
      subject = null;
      start = null;
      description = null;
      end = null;
      location = null;
      status = null;
      identifier = null;
    }

    /**
     * Method to change the subject of an event.
     *
     * @param subject to replace the current event subject
     * @return updated Event object with new subject
     */
    public EventBuilder subject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Method to change the start time of an event.
     *
     * @param start to replace the current start time of an event
     * @return updated Event object with new start time
     */
    public EventBuilder start(LocalDateTime start) {
      this.start = start;
      return this;
    }

    /**
     * Method to change the description of an event.
     *
     * @param description to replace the current event description
     * @return updated Event object with new description
     */
    public EventBuilder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Method to change the end time of an event.
     *
     * @param end to replace the current end time of an event
     * @return updated Event object with new end time
     */
    public EventBuilder end(LocalDateTime end) {
      this.end = end;
      return this;
    }

    /**
     * Method to change the location of an event.
     *
     * @param location to replace the current location of an event
     * @return updated Event object with new location
     */
    public EventBuilder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Method to change the status time of an event.
     *
     * @param status to replace the current status of an event
     * @return updated Event object with new status
     */
    public EventBuilder status(String status) {
      this.status = status;
      return this;
    }

    /**
     * Method to change the identifier of the event.
     *
     * @param identifier of the event
     * @return updated Event object with new status
     */
    public EventBuilder identifier(UUID identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * Builder method for Events.
     *
     * @return the Event with all the updated parameters
     */
    public Event build() {
      return new Event(subject, start, description,
              end, location, status, identifier);
    }
  }

  /**
   * Get start time of event.
   *
   * @return start time
   */
  public LocalDateTime getStart() {
    return start;
  }

  /**
   * Get end time of event.
   *
   * @return end time
   */
  public LocalDateTime getEnd() {
    return end;
  }

  /**
   * Get subject of event.
   *
   * @return event subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Get description of event.
   *
   * @return event description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get UUID of event.
   *
   * @return event identifier
   */
  public UUID getIdentifier() {
    return identifier;
  }

  /**
   * Get status of event.
   *
   * @return event status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Get location of event.
   *
   * @return event location
   */
  public String getLocation() {
    return location;
  }
}
