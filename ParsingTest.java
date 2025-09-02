import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Test class to check the InputParsing class (if each command type is read correctly).
 */
public class ParsingTest {

  @Test
  public void testExit() {
    String commandTest =
            "exit";
    String[] test = commandTest.split(" ");

    assertEquals(0,InputParsing.commandType(test));
  }

  @Test
  public void testCase3() {
    String commandTest =
            "create event bleh1 from 2025-06-05T14:15 to 2025-06-05T15:15";
    String[] test = commandTest.split(" ");

    assertEquals(3,InputParsing.commandType(test));
  }

  @Test
  public void testCase4() {
    String commandTest =
            "create event bleh2 from 2025-06-06T14:15 to 2025-06-06T15:15 repeats F for 6 times";
    String[] test = commandTest.split(" ");

    assertEquals(4,InputParsing.commandType(test));
  }

  @Test
  public void testCase5() {
    String commandTest =
            "create event b3 from 2025-06-07T14:15 to 2025-06-07T15:15 repeats S until 2025-06-28";
    String[] test = commandTest.split(" ");

    assertEquals(5,InputParsing.commandType(test));
  }

  @Test
  public void testCase6() {
    String commandTest =
            "create event bleh4 on 2025-06-05";
    String[] test = commandTest.split(" ");

    assertEquals(6,InputParsing.commandType(test));
  }

  @Test
  public void testCase7() {
    String commandTest =
            "create event bleh5 on 2025-06-06 repeats S for 10 times";
    String[] test = commandTest.split(" ");

    assertEquals(7,InputParsing.commandType(test));
  }

  @Test
  public void testCase8() {

    String commandTest =
            "create event bleh6 on 2025-06-07 repeats S until 2025-06-28";
    String[] test = commandTest.split(" ");

    assertEquals(8,InputParsing.commandType(test));
  }

  @Test
  public void testCase9() {

    String commandTest =
            "edit event description bleh1 from 2025-06-05T14:15 to 2025-06-05T15:15 with :|";
    String[] test = commandTest.split(" ");

    assertEquals(9,InputParsing.commandType(test));

  }

  @Test
  public void testCase10() {
    String commandTest =
            "edit event description bleh2 from 2025-06-13T14:15 with :P";
    String[] test = commandTest.split(" ");

    assertEquals(10,InputParsing.commandType(test));
  }

  @Test
  public void testCase11() {
    String commandTest =
            "edit series description bleh3 from 2025-06-14T14:15 with :D";
    String[] test = commandTest.split(" ");

    assertEquals(11,InputParsing.commandType(test));
  }

  @Test
  public void testCase12() {
    String commandTest =
            "print events on 2025-06-13";
    String[] test = commandTest.split(" ");

    assertEquals(12,InputParsing.commandType(test));
  }

  @Test
  public void testCase13() {
    String commandTest =
            "print events from 2025-06-05T14:15 to 2025-06-28T14:15";
    String[] test = commandTest.split(" ");

    assertEquals(13,InputParsing.commandType(test));
  }

  @Test
  public void testCase14() {
    String commandTest =
            "show status on 2025-06-05T14:15";
    String[] test = commandTest.split(" ");

    assertEquals(14,InputParsing.commandType(test));
  }

  @Test
  public void testCase15() {
    String commandTest =
            "create calendar --name bleh --timezone Pacific/Tahiti";
    String[] test = commandTest.split(" ");

    assertEquals(15, InputParsing.commandType(test));
  }

  @Test
  public void testCase16() {
    String commandTest =
            "edit calendar --name bleh --property timezone Europe/Brussels";
    String[] test = commandTest.split(" ");

    assertEquals(16, InputParsing.commandType(test));
  }

  @Test
  public void testCase17() {
    String commandTest =
            "use calendar --name bleh";
    String[] test = commandTest.split(" ");

    assertEquals(17, InputParsing.commandType(test));
  }

  @Test
  public void testCase18() {
    String commandTest =
            "copy event ee on 2025-06-05T14:15 --target aa to 2025-06-28T14:15";
    String[] test = commandTest.split(" ");

    assertEquals(18, InputParsing.commandType(test));
  }

  @Test
  public void testCase19() {
    String commandTest =
            "copy events on 2025-06-05 --target eg to 2025-06-05";
    String[] test = commandTest.split(" ");

    assertEquals(19, InputParsing.commandType(test));
  }

  @Test
  public void testCase20() {
    String commandTest =
            "copy events between 2025-06-05 and 2025-06-08 --target aa to 2025-06-28";
    String[] test = commandTest.split(" ");

    assertEquals(20, InputParsing.commandType(test));
  }



}