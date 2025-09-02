import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Class to determine which (if any) valid command format has been used.
 */
public class InputParsing {

  /**
   * Bank of correct formatting for commands.
   */
  public static final String[][] COMMAND_BANK = {
          {"exit"}, // 0
          {"list"}, // 1 (to implement later)
          {"exe"}, // 2 (to implement later)
          {"create", "event", "", "from", "dt", "to", "dt"}, // 3
          {"create", "event", "", "from", "dt", "to", "dt",
          "repeats", "w", "for", "n", "times"}, // 4
          {"create", "event", "", "from", "dt", "to", "dt", "repeats", "w", "until", "d"}, // 5
          {"create", "event", "", "on", "d"}, // 6
          {"create", "event", "", "on", "d", "repeats", "w", "for", "n", "times"}, // 7
          {"create", "event", "", "on", "d", "repeats", "w", "until", "d"}, // 8
          {"edit", "event", "p", "", "from", "dt", "to", "dt", "with", ""}, // 9
          {"edit", "event", "p", "", "from", "dt", "with", ""}, // 10
          {"edit", "series", "p", "", "from", "dt", "with", ""}, // 11
          {"print", "events", "on", "d"}, // 12
          {"print", "events", "from", "dt", "to", "dt"}, // 13
          {"show", "status", "on", "dt"}, // 14
          //cn = calendar name
          //al = area/location
          //pn = property name (larger cal)
          {"create", "calendar", "--name", "cn", "--timezone", "al"}, // 15
          {"edit", "calendar", "--name", "cn", "--property", "pn", ""}, //16
          {"use", "calendar", "--name", "cn"}, //17
          {"copy", "event", "", "on", "dt", "--target", "cn", "to", "dt"}, //18
          {"copy", "events", "on", "d", "--target", "cn", "to", "d"}, //19
          {"copy", "events", "between", "d", "and", "d", "--target", "cn", "to", "d"} //20
  };

  /**
   * Bank of valid properties that can change in a calendar.
   */
  public static final String[] C_PROP_BANK = {
      "timezone", "name"
  };

  /**
   * Bank of valid command parameters for editing.
   */
  public static final String[] P_BANK = {
      "start", "end", "description", "location", "status"
  };

  /**
   * Bank of days of the week.
   */
  public static final String[] W_BANK = {
      "M", "T", "W", "R", "F", "S", "U"
  };

  /**
   * Bank with the number of days in each month.
   */
  public static final int[] DAYS_IN_MONTH = {
      31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
  };


  private static boolean validNum(String n) {
    int num;
    try {
      num = Integer.parseInt(n);
    } catch (NumberFormatException e) {
      num = -1;
    }
    return num > 0;

  }

  private static boolean checkD(String d) {
    boolean valid = false;

    try {
      if (d.length() == 10
              && validNum(d.substring(0, 4))
              && d.charAt(4) == '-'
              && validNum(d.substring(5, 7))
              && Integer.parseInt(d.substring(5, 7)) < 13
              && d.charAt(7) == '-'
              && validNum(d.substring(8, 10))
      ) {
        int year = Integer.parseInt(d.substring(0, 4));
        int month = Integer.parseInt(d.substring(5, 7));
        int day = Integer.parseInt(d.substring(8, 10));

        if (month == 2) {
          if (year % 4 == 0 && year % 100 != 0) {
            if (day <= 29) {
              valid = true;
            }

          } else {
            if (day <= DAYS_IN_MONTH[month - 1]) {
              valid = true;
            }

          }

        } else {

          if (day <= DAYS_IN_MONTH[month - 1]) {
            valid = true;
          }

        }

      }
    } catch (StringIndexOutOfBoundsException e) {
      valid = false;

    }

    return valid;

  }

  private static boolean checkDT(String dt) {
    try {
      return checkD(dt.substring(0, 10))
              && dt.charAt(10) == 'T'
              && validNum(dt.substring(11, 13))
              && Integer.parseInt(dt.substring(11, 13)) < 25
              && dt.charAt(13) == ':'
              && validNum(dt.substring(14, 16))
              && Integer.parseInt(dt.substring(14, 16)) < 61;
    } catch (StringIndexOutOfBoundsException e) {
      return false;

    }

  }

  /**
   * Converts a string into a LocalDateTime object.
   *
   * @param dt String to be converted into LocalDateTime
   * @return a LocalDateTime object representing the inputted date
   */
  public static LocalDateTime createLocalDateTime(String dt) {
    int year = Integer.parseInt(dt.substring(0, 4));
    int month = Integer.parseInt(dt.substring(5, 7));
    int day = Integer.parseInt(dt.substring(8, 10));
    int hour = Integer.parseInt(dt.substring(11, 13));
    int minute = Integer.parseInt(dt.substring(14, 16));

    return LocalDateTime.of(year, month, day, hour, minute);

  }

  /**
   * Converts a String into a LocalDate object.
   *
   * @param dt String to be converted into LocalDate
   * @return a LocalDate object representting the inputted date
   */
  public static LocalDate createLocalDate(String dt) {
    int year = Integer.parseInt(dt.substring(0, 4));
    int month = Integer.parseInt(dt.substring(5, 7));
    int day = Integer.parseInt(dt.substring(8, 10));

    return LocalDate.of(year, month, day);
  }

  private static boolean afterDT(String dt1, String dt2) {
    LocalDateTime date1 = createLocalDateTime(dt1);
    LocalDateTime date2 = createLocalDateTime(dt2);
    return date1.isAfter(date2);

  }

  private static boolean sameD(String dt1, String dt2) {
    String d1 = dt1.substring(0, 10);
    String d2 = dt2.substring(0, 10);
    return d1.equals(d2);

  }

  private static boolean wMatch(String w, String dt) {
    int wInt = -1;
    for (int i = 0; i < W_BANK.length; i++) {
      if (W_BANK[i].equals(w)) {
        wInt = i + 1;
      }

    }
    int dtInt = createLocalDateTime(dt).getDayOfWeek().getValue();
    return wInt == dtInt;

  }

  private static boolean containsString(String[] arr, String str) {
    for (String string : arr) {
      if (string.equals(str)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines which type of command is being inputted, if at all.
   *
   * @param command list of words in the command
   * @return type of command (int); -1 if not a valid command
   */
  public static int commandType(String[] command) {
    int type = -1;

    for (int i = 0; i < COMMAND_BANK.length; i++) {
      if (COMMAND_BANK[i].length == command.length) {
        boolean match = true;
        for (int j = 0; j < COMMAND_BANK[i].length; j++) {
          if (!((COMMAND_BANK[i][j].isEmpty())
                  || (COMMAND_BANK[i][j].equals("p") && containsString(P_BANK, command[j]))
                  || (COMMAND_BANK[i][j].equals("d") && checkD(command[j]))
                  || (COMMAND_BANK[i][j].equals("dt") && checkDT(command[j]))
                  || (COMMAND_BANK[i][j].equals("w") && containsString(W_BANK, command[j]))
                  || (COMMAND_BANK[i][j].equals("n") && validNum(command[j]))
                  || (COMMAND_BANK[i][j].equals("cn"))
                  || ((COMMAND_BANK[i][j].equals("al")))
                  || (COMMAND_BANK[i][j].equals("pn") && containsString(C_PROP_BANK, command[j]))
                  || (COMMAND_BANK[i][j].equals(command[j])))
          ) {
            match = false;
            break;
          }
        }

        if (type == 3) {
          if (match && afterDT(command[4], command[6])) {
            type = i;
          }

        } else if (type == 4) {
          if (match && afterDT(command[4], command[6])
                  && sameD(command[4], command[6])
                  && wMatch(command[8], command[4])) {
            type = i;
          }

        } else if (type == 5) {
          if (match && afterDT(command[4], command[6])
                  && afterDT(command[6], command[10] + "T00:00")
                  && sameD(command[4], command[6])
                  && wMatch(command[8], command[4])) {
            type = i;
          }

        } else if (type == 8) {
          if (match && wMatch(command[6], command[8])) {
            type = i;
          }

        } else if (type == 9) {
          if (match && afterDT(command[5], command[7])) {
            if (command[3].equals("start") || command[3].equals("end")) {
              if (checkDT(command[9])) {
                type = i;
              }

            } else {
              type = i;

            }
          }

        } else if (type == 10) {
          if (match) {
            if (command[3].equals("start") || command[3].equals("end")) {
              if (checkDT(command[7])) {
                type = i;
              }

            } else {
              type = i;

            }
          }


        } else if (type == 11) {
          if (match) {
            if (command[3].equals("start") || command[3].equals("end")) {
              if (checkDT(command[7])) {
                type = i;
              }

            } else {
              type = i;

            }
          }

        } else if (type == 13) {
          if (match && afterDT(command[3], command[5])) {
            type = i;
          }

        } else {
          if (match) {
            type = i;
          }

        }

      }

    }

    return type;

  }

  /**
   * Returns user specifications from command.
   *
   * @param input from user as a String
   * @return the command parameters relevant to each command
   */
  public static String[] parseCommand(String input) {
    String[] command = input.split(" ");
    int type = commandType(command);
    String[] parsedCommand = {};

    if (type == -1) {
      parsedCommand = new String[]{"-1"};

    } else if (type == 0) {
      parsedCommand = new String[]{"0"};

    } else if (type == 1) {
      parsedCommand = new String[]{"1"};

    } else if (type == 2) {
      parsedCommand = new String[]{"2"};

    } else if (type == 3) {
      parsedCommand = new String[]{"3", command[2], command[4], command[6]};

    } else if (type == 4) {
      parsedCommand = new String[]
          {"4", command[2], command[4], command[6], command[8], command[10]};

    } else if (type == 5) {
      parsedCommand = new String[]
          {"5", command[2], command[4], command[6], command[8], command[10]};

    } else if (type == 6) {
      parsedCommand = new String[]{"6", command[2], command[4]};

    } else if (type == 7) {
      parsedCommand = new String[]{"7", command[2], command[4], command[6], command[8]};

    } else if (type == 8) {
      parsedCommand = new String[]{"8", command[2], command[4], command[6], command[8]};

    } else if (type == 9) {
      parsedCommand = new String[]{"9", command[2], command[3], command[5], command[7], command[9]};

    } else if (type == 10) {
      parsedCommand = new String[]{"10", command[2], command[3], command[5], command[7]};

    } else if (type == 11) {
      parsedCommand = new String[]{"11", command[2], command[3], command[5], command[7]};

    } else if (type == 12) {
      parsedCommand = new String[]{"12", command[3]};

    } else if (type == 13) {
      parsedCommand = new String[]{"13", command[3], command[5]};

    } else if (type == 14) {
      parsedCommand = new String[]{"14", command[3]};


      //NEW STUFF BEGINS HERE
    } else if (type == 15) {
      parsedCommand = new String[]{"15", command[3], command[5]};

    } else if (type == 16) {
      parsedCommand = new String[]{"16", command[3], command[5], command[6]};

    } else if (type == 17) {
      parsedCommand = new String[]{"17", command[3]};

    } else if (type == 18) {
      parsedCommand = new String[]{"18", command[2], command[4], command[6], command[8]};

    } else if (type == 19) {
      parsedCommand = new String[]{"19", command[3], command[5], command[7]};

    } else if (type == 20) {
      parsedCommand = new String[]{"20", command[3], command[5], command[7], command[9]};

    }



    return parsedCommand;

  }
}
