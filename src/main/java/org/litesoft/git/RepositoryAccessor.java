package org.litesoft.git;

import java.util.Map;

import org.litesoft.annotations.Mutable;
import org.litesoft.annotations.NotEmpty;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;

@SuppressWarnings("unused")
public interface RepositoryAccessor {

  @NotNull @Mutable
  Map<String, Repository> getRepositories();

  @NotNull
  Repository.Builder createRepository( @Significant String pName );

  /**
   * File Path (with name and optional extension) within Repo.
   *
   * @param pFilePath must be significant.
   */
  @NotNull
  CreatableFile.Builder createFileBuilder( @Significant String pFilePath );

  @NotEmpty
  String getDisplayRef();
}
