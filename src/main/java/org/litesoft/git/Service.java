package org.litesoft.git;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.litesoft.annotations.NotEmpty;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.minimal_ioutils.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Service {
  private final RepositoryAccessor mRepositoryAccessor;
  private final Map<String, Repository> mRepositories;

  public Service( @NotNull RepositoryAccessor pRepositoryAccessor ) {
    mRepositoryAccessor = NotNull.AssertArgument.namedValue( "RepositoryAccessor", pRepositoryAccessor );
    mRepositories = mRepositoryAccessor.getRepositories();
  }

  @NotEmpty
  public String getDisplayRef() {
    return mRepositoryAccessor.getDisplayRef();
  }

  @NotNull
  public List<String> getRepositoryNames() {
    return new ArrayList<>( mRepositories.keySet() );
  }

  @NotNull
  public List<Repository> getRepositories() {
    return new ArrayList<>( mRepositories.values() );
  }

  @NotNull
  public Map<String, Repository> getRepositoryMap() {
    return new TreeMap<>( mRepositories );
  }

  public void removeRepositories( @Nullable Logger pLogger, @NotNull RepoFilter pFilter ) {
    pLogger = Logger.deNull( pLogger );
    NotNull.AssertArgument.namedValue( "Filter", pFilter );

    List<String> zDeleted = new ArrayList<>();
    for ( Map.Entry<String, Repository> zEntry : mRepositories.entrySet() ) {
      Repository zRepository = zEntry.getValue();
      if ( pFilter.test( zRepository ) ) {
        deleteRepo( pLogger, zRepository );
        zDeleted.add( zEntry.getKey() );
      }
    }
    if ( !zDeleted.isEmpty() ) {
      for ( String zKey : zDeleted ) {
        mRepositories.remove( zKey );
      }
    }
  }

  private void deleteRepo( Logger pLogger, Repository pRepository ) {
    pLogger.log( "   Deleting: " + pRepository.getName() );
    pRepository.delete();
  }

  @SuppressWarnings("UnusedReturnValue")
  @Nullable
  public Repository createRepo( @Nullable Logger pLogger, @Significant String pRepoName, @NotEmpty String pDescription,
                                @Nullable CreatableFile pCreatableFile ) {
    pLogger = Logger.deNull( pLogger );
    pRepoName = Significant.AssertArgument.namedValue( "RepoName", pRepoName );
    pDescription = NotEmpty.AssertArgument.namedValue( "Description", pDescription );
    if ( mRepositories.containsKey( pRepoName ) ) {
      pLogger.log( "   Already Exists: " + pRepoName );
      return null;
    }
    pLogger.log( "   Creating: " + pRepoName );

    Repository.Builder zBuilder = mRepositoryAccessor.createRepository( pRepoName ).description( pDescription );
    if ( pCreatableFile != null ) {
      zBuilder.initWithDescriptionAsReadMe();
    }
    Repository zRepo = zBuilder.create();

    if ( pCreatableFile != null ) {
      pLogger.log( "       Adding: " + pCreatableFile.getFilePath() );
      zRepo.createFile( pCreatableFile );
    }

    return zRepo;
  }
}
