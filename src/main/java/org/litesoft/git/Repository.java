package org.litesoft.git;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;

@SuppressWarnings("unused")
public interface Repository {

  @Significant
  String getName();

  @Significant
  String getFullName();

  @SuppressWarnings("unused")
  void delete();

  /**
   * File Path (with name and optional extension) within Repo.
   *
   * @param pFilePath must be significant.
   */
  @NotNull
  CreatableFile.Builder createFileBuilder( @Significant String pFilePath );

  void createFile( @NotNull CreatableFile pCreatableFile );

  interface Builder {
    Builder description( @Significant String pDescription );

    Builder initWithDescriptionAsReadMe();

    @NotNull
    Repository create();
  }
}
