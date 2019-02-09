package org.litesoft.minimal_ioutils;

import java.io.PrintStream;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;

public interface Logger {
  void log( String pMessage );

  @NotNull
  static Logger deNull( @Nullable Logger pLogger ) {
    return (pLogger != null) ? pLogger : createFrom( null );
  }

  @NotNull
  static Logger createFrom( @Nullable PrintStream pPrintStream ) {
    return (pPrintStream != null) ?
           pPrintStream::println :
           (T -> {}); // No Op!
  }
}
