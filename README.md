# Calendar

Calendar part 3 assignment for Object Oriented Design.


## How to run program

To run the program, run Main.java, which begins the rest of the program.

For interactive mode, simply type commands into the console.

For headless mode, the program will read from an inputted ``.txt`` file for the commands.

For GUI mode, a GUI will be opened that the user can create.

## Commands for interactive/headless mode

``create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString>`` - Creates a single event in the calendar.

``create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times`` - Creates an event series that repeats N times on specific weekdays. Note \<weekdays> is a sequence of characters where character denotes a day of the week, e.g., MRU. 'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday, 'F' is Friday, 'S' is Saturday, and 'U' is Sunday.

``create event <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateString>`` - Creates an event series until a specific date (inclusive).

``create event <eventSubject> on <dateString>`` - Creates a single all-day event.

``create event <eventSubject> on <dateString> repeats <weekdays> for <N> times`` - Creates a series of all day events that repeats N times on specific weekdays.

``create event <eventSubject> on <dateString> repeats <weekdays> until <dateString>`` - Creates a series of all day events until a specific date (inclusive).

``edit event <property> <eventSubject> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>`` - Changes the property of the given event (irrespective of whether it is single or part of a series).

``edit events <property> <eventSubject> from <dateStringTtimeString> with <NewPropertyValue>`` - Identify the event that has the given subject and starts at the given date and time and edit its property. If this event is part of a series then the properties of all events in that series that start at or after the given date and time should be changed. If this event is not part of a series then this has the same effect as the command above.

``edit series <property> <eventSubject> from <dateStringTtimeString> with <NewPropertyValue>`` - Identify the event that has the given subject and starts at the given date and time and edit its property. If this event is part of a series then the properties of all events in that series should be changed. If this event is not part of a series then this has the same effect as the first edit command.

For all these queries the ``<property>`` field may be one of the following: ``start, end, description, location, status.`` 

``print events on <dateString>`` - Prints a bulleted list of all events on that day along with their start and end time and location (if any).

``print events from <dateStringTtimeString> to <dateStringTtimeString>`` - Prints a bulleted list of all events in the given interval including their start and end times and location (if any).

``show status on <dateStringTtimeString>`` - Prints busy status if the user has events scheduled on a given day and time, otherwise, available.

``create calendar --name <calName> --timezone area/location`` - Create a calendar with specified name and timezone.

``edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>`` - Edit calendar name or timezone.

``use calendar --name <name-of-calendar>`` - Use calendar if this calendar exists.

``copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>`` -- Copy a single event.

``copy events on <dateString> --target <calendarName> to <dateString>`` - Copy a day of events. Shift time is timezone of target calendar is different.

``copy events between <dateString> and <dateString> --target <calendarName> to <dateString>`` - Copy multiple days of events.

## Work distribution

Solo group; Emily wrote everything.

## Changes to program design

Creation of GUI mode: GUI was created for this part of the project. This does not explicitly affect any of the other code, except for one thing: RunCalendar.java has now been modified to handle the possibility of a GUI mode being selected. 

This includes:

- An addition of a blank entry as an acceptable user selection. This opens up the GUI menu for the calendar; this was chosen so the original functionality of interactive and headless mode could still be used if chosen, but GUI was also added as a working option for the user.

- GUI methods. In the GUI, users can add and modify single events, as well as see a schedule view of up to 10 events starting from a certain date.


