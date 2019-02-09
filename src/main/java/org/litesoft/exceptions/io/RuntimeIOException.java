package org.litesoft.exceptions.io;

import java.io.IOException;

public class RuntimeIOException extends RuntimeException {
  @SuppressWarnings("unused")
  public RuntimeIOException( String message ) {
    super( message );
  }

  @SuppressWarnings("WeakerAccess")
  public RuntimeIOException( IOException cause ) {
    super( cause );
  }
}
