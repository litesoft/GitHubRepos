package org.litesoft.student;

import java.util.List;
import java.util.function.Predicate;

import org.litesoft.annotations.Nullable;
import org.litesoft.git.RepoFilter;
import org.litesoft.git.Repository;

@SuppressWarnings("unused")
public class StudentRepoFilter implements RepoFilter {
  private final String[] mPrefixes;
  private final Predicate<String> mSeatTest;

  public StudentRepoFilter( List<String> pPrefixes, Predicate<String> pSeatTest ) {
    mPrefixes = pPrefixes.toArray( new String[0] );
    mSeatTest = pSeatTest;
  }

  @Override
  public boolean test( @Nullable Repository pRepository ) {
    String zSuffix = checkStartsWith( pRepository, mPrefixes );
    return (zSuffix != null) && mSeatTest.test( zSuffix ); // Left to Right!
  }
}
