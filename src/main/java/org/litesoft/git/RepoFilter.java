package org.litesoft.git;

import java.util.function.Predicate;

import org.litesoft.annotations.Nullable;

public interface RepoFilter extends Predicate<Repository> {
  /**
   * @param pRepository to be checked
   * @param pPrefixes   value(s) to check if the name of the <code>Repository</code> Starts With.
   *
   * @return null if no match, otherwise the portion of the name AFTER the matching Starts With <code>Prefix</code>
   */
  @Nullable
  default String checkStartsWith( @Nullable Repository pRepository, @Nullable String... pPrefixes ) {
    if ( (pRepository != null) && (pPrefixes != null) && (pPrefixes.length > 0) ) { // Left to Right!
      String zName = pRepository.getName();
      for ( String zPrefix : pPrefixes ) {
        if ( (zPrefix != null) && !zPrefix.isEmpty() && zName.startsWith( zPrefix ) ) { // Left to Right!
          return zName.substring( zPrefix.length() );
        }
      }
    }
    return null;
  }
}
