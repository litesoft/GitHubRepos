package org.litesoft.exceptions.io;

import java.io.IOException;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.annotations.SignificantOrNull;

public class IOExceptionConverter {
  public static IOException withSource( @SignificantOrNull String pSource, @NotNull Exception pException ) {
    NotNull.AssertArgument.namedValue( "Exception", pException );
    pSource = Significant.ConstrainTo.valueOrNull( pSource );
    if ( pSource != null ) {
      return new IOException( pException.getMessage() + " while processing: " + pSource );
    }
    if ( pException instanceof IOException ) {
      return (IOException)pException;
    }
    return new IOException( pException );
  }
}
