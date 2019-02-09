package org.litesoft.student;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.git.RepoFilter;
import org.litesoft.git.Repository;

public class LegacyStudentFilter implements RepoFilter {
  private static final String[] PREFIXES = {"Student", "student"};
  private static final int BAD_DIGIT_VALUE = -1000; // Big enough that subtracting (10 * 9) is still negative!
  private static final int MAX_STUDENT_ID = 25; // Big enough that subtracting (10 * 9) is still negative!
  private static final int MIN_STUDENT_ID = 2; // Should be 1, but currently preserving Student1

  @Override
  public boolean test( @Nullable Repository pRepository ) {
    String zSuffix = checkStartsWith( pRepository, PREFIXES );
    return (zSuffix != null) && isStudentID( zSuffix ); // Left to Right!
  }

  private boolean isStudentID( String pStudentID ) {
    pStudentID = NotNull.ConstrainTo.valueOr( pStudentID, "" );
    int zID = 0;
    int zLength = pStudentID.length();
    if ( zLength == 2 ) {
      zID += 10 * digit( pStudentID.charAt( --zLength ) );
    }
    if ( zLength == 1 ) {
      zID += digit( pStudentID.charAt( --zLength ) );
    }
    return acceptableID( zID );
  }

  private boolean acceptableID( int pStudentID ) {
    return (MIN_STUDENT_ID <= pStudentID) && (pStudentID <= MAX_STUDENT_ID);
  }

  private int digit( char c ) {
    return !Character.isDigit( c ) ? BAD_DIGIT_VALUE : (c - '0');
  }
}
