package temp;

// A Label represents an address in assembly language. (p. 131)

public class Label {
  private String name;
  private static int count;

  // A printable representation of the label, for use in assembly language.
  public String toString() {return name;}

  // Make a new Label with name n.
  // Note that multiply-defined labels must be avoided!
  public Label(String n) {
    name=n;
  }

  // Generate a fresh Label like "L27".
  public Label() {
    this("L" + count++);
  }
}
