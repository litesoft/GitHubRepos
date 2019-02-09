package org.litesoft.exceptions.io;

import java.io.IOException;

@FunctionalInterface
public interface IOExceptionalSupplier<T> {

  /**
   * Interface to represent a Process that returns <code>T</code>, AND can throw an IOException.
   * Gets a result.
   *
   * @return a result
   */
  T get()
          throws IOException;

  class Wrapper {
    public static <T> T process( IOExceptionalSupplier<T> pToCall ) {
      try {
        return pToCall.get();
      }
      catch ( IOException e ) {
        throw new RuntimeIOException( e );
      }
    }
  }
}
