package org.litesoft.github;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.git.CreatableFile;

@SuppressWarnings("unused")
public interface CreatableFileBuilderFactory {
  /**
   * File Path (with nam and optional extension) within Repo.
   *
   * @param pFilePath must be significant.
   *
   * @return Not Null Creatable File Builder
   */
  @NotNull
  CreatableFile.Builder create( @Significant String pFilePath );
}
