package org.litesoft.exceptions.io;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionalExecutor {

  /**
   * Interface to represent a Process that returns nothing, but can throw an IOException.
   */
  void execute()
          throws IOException;

  class Wrapper {
    public static void process( IOExceptionalExecutor pToCall ) {
      try {
        pToCall.execute();
      }
      catch ( IOException e ) {
        throw new RuntimeIOException( e );
      }
    }
  }
}
