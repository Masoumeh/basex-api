/**
 * GUI start class, which includes all module libraries for convenient testing.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public final class StartCLI {
  /** Private constructor. */
  private StartCLI() { }

  /**
   * Main method.
   * @param args command-line arguments
   * @throws Exception exception
   */
  public static void main(final String... args) throws Exception {
    new org.basex.BaseX(args);
  }
}
