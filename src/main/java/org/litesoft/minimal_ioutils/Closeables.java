package org.litesoft.minimal_ioutils;

import java.io.Closeable;

import org.litesoft.annotations.Nullable;
import org.litesoft.exceptions.Ignore;

@SuppressWarnings("unused")
public class Closeables {

  @SuppressWarnings("UnusedReturnValue")
  @Nullable
  public static Closeable closeQuietly( @Nullable Closeable pCloseable ) {
    if ( pCloseable != null ) {
      try {
        pCloseable.close();
      }
      catch ( Exception toIgnore ) {
        Ignore.it( toIgnore );
      }
    }
    return null;
  }
}
