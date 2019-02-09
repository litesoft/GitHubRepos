package org.litesoft.git;

import java.util.List;
import java.util.Map;

import org.litesoft.annotations.Mutable;
import org.litesoft.annotations.NotEmpty;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.annotations.SignificantOrNull;

@SuppressWarnings("unused")
public interface Git {
  @NotNull
  @Mutable
  List<String> getOrganizationNames();

  @Nullable
  Organization getOrganization( @SignificantOrNull String pName );

  @SignificantOrNull
  String getLogin();

  @NotNull
  Repository.Builder createRepositoryOutsideOfOrganization( @Significant String pName );

  @NotNull
  @Mutable
  Map<String, Repository> getRepositories();

  /**
   * File Path (with name and optional extension) within Repo.
   *
   * @param pFilePath must be significant.
   */
  @NotNull
  CreatableFile.Builder createFileBuilder( @Significant String pFilePath );

  @NotEmpty
  default String getDisplayRef() {
    return "(login)" + getLogin();
  }

  @NotNull
  default RepositoryAccessor asRepositoryAccessor() {
    return new RepositoryAccessor() {
      @Override
      public Map<String, Repository> getRepositories() {
        return Git.this.getRepositories();
      }

      @Override
      public Repository.Builder createRepository( String pName ) {
        return createRepositoryOutsideOfOrganization( pName );
      }

      @Override
      public CreatableFile.Builder createFileBuilder( String pFilePath ) {
        return Git.this.createFileBuilder( pFilePath );
      }

      @Override
      public String getDisplayRef() {
        return Git.this.getDisplayRef();
      }
    };
  }
}
