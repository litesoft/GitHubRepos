package org.litesoft.github;

import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.litesoft.annotations.NotNull;
import org.litesoft.git.Repository;

@SuppressWarnings("unused")
public interface RepoBuilderFactory {
  /**
   * Create a Repository.Builder.
   *
   * @param pBuilder                     Not Null underlying Builder
   * @param pCreatableFileBuilderFactory Not Null Creatable File Builder Factory
   *
   * @return Not Null
   */
  @NotNull
  Repository.Builder create( @NotNull GHCreateRepositoryBuilder pBuilder,
                             @NotNull CreatableFileBuilderFactory pCreatableFileBuilderFactory );
}
