package org.litesoft.github;

import java.util.Map;

import org.kohsuke.github.GHOrganization;
import org.litesoft.annotations.NotEmpty;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.git.CreatableFile;
import org.litesoft.git.Organization;
import org.litesoft.git.Repository;

@SuppressWarnings("WeakerAccess")
public class OrganizationImpl implements Organization {
  private final GHOrganization mOrg;
  private final String mName;
  private final RepoBuilderFactory mRepoBuilderFactory;
  private final CreatableFileBuilderFactory mCreatableFileBuilderFactory;

  protected OrganizationImpl( @NotNull GHOrganization pOrg, @NotEmpty String pName,
                              @NotNull RepoBuilderFactory pRepoBuilderFactory,
                              @NotNull CreatableFileBuilderFactory pCreatableFileBuilderFactory ) {
    mOrg = NotNull.AssertArgument.namedValue( "Org", pOrg );
    mName = NotNull.AssertArgument.namedValue( "Name", pName );
    mRepoBuilderFactory = NotNull.AssertArgument.namedValue( "RepoBuilderFactory", pRepoBuilderFactory );
    mCreatableFileBuilderFactory = NotNull.AssertArgument.namedValue( "CreatableFileBuilderFactory", pCreatableFileBuilderFactory );
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public String toString() {
    return "Org: " + getName();
  }

  @Override
  public Map<String, Repository> getRepositories() {
    return RepositoryImpl.map( mCreatableFileBuilderFactory,
                               mOrg::listRepositories );
  }

  @Override
  public Repository.Builder createRepository( String pName ) {
    String zName = Significant.AssertArgument.namedValue( "Name", pName );
    return mRepoBuilderFactory.create( mOrg.createRepository( zName ),
                                       mCreatableFileBuilderFactory );
  }

  @Override
  public CreatableFile.Builder createFileBuilder( @Significant String pFilePath ) {
    return mCreatableFileBuilderFactory.create( Significant.AssertArgument.namedValue( "FilePath", pFilePath ) );
  }
}
