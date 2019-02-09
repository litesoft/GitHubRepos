package org.litesoft.github;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.kohsuke.github.GHContentBuilder;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.exceptions.io.RuntimeIOException;
import org.litesoft.git.CreatableFile;
import org.litesoft.git.Repository;

import static org.litesoft.exceptions.io.IOExceptionalExecutor.Wrapper.process;
import static org.litesoft.exceptions.io.IOExceptionalSupplier.Wrapper.process;

@SuppressWarnings("WeakerAccess")
public class RepositoryImpl implements Repository {
  private final GHRepository mRepo;
  private final CreatableFileBuilderFactory mCreatableFileBuilderFactory;

  protected RepositoryImpl( @NotNull GHRepository pRepo,
                            @NotNull CreatableFileBuilderFactory pCreatableFileBuilderFactory ) {
    mRepo = NotNull.AssertArgument.namedValue( "Repo", pRepo );
    mCreatableFileBuilderFactory = NotNull.AssertArgument.namedValue( "CreatableFileBuilderFactory",
                                                                      pCreatableFileBuilderFactory );
  }

  @Override
  public String getName() {
    return mRepo.getName();
  }

  @Override
  public String getFullName() {
    return mRepo.getFullName();
  }

  @Override
  public String toString() {
    return "Repo: " + getFullName();
  }

  @Override
  public void delete() {
    process( mRepo::delete );
  }

  @Override
  public CreatableFile.Builder createFileBuilder( @Significant String pFilePath ) {
    return mCreatableFileBuilderFactory.create( Significant.AssertArgument.namedValue( "FilePath", pFilePath ) );
  }

  @Override
  public void createFile( @NotNull CreatableFile pCreatableFile ) {
    NotNull.AssertArgument.namedValue( "CreatableFile", pCreatableFile );
    GHContentBuilder zBuilder = mRepo.createContent()
            .path( pCreatableFile.getFilePath() )
            .message( pCreatableFile.getCommitMessage() );
    String zTextContents = pCreatableFile.getTextFileContents();
    if ( zTextContents != null ) {
      zBuilder.content( zTextContents );
    } else {
      zBuilder.content( pCreatableFile.getBinaryFileContents() );
    }

    GHContentUpdateResponse zResponse = process( zBuilder::commit );
    if ( zResponse == null ) {
      throw new RuntimeIOException( "createFile's 'commit' did not return a 'Commit'" );
    }
  }

  @SuppressWarnings("unused")
  private static boolean alwaysTruePredicate( GHRepository pRepository ) {
    return true;
  }

  protected static Map<String, Repository> map( CreatableFileBuilderFactory pCreatableFileBuilderFactory,
                                                Supplier<PagedIterable<GHRepository>> pRepoSupplier ) {
    return map( pCreatableFileBuilderFactory, pRepoSupplier, RepositoryImpl::alwaysTruePredicate );
  }

  protected static Map<String, Repository> map( CreatableFileBuilderFactory pCreatableFileBuilderFactory,
                                                Supplier<PagedIterable<GHRepository>> pRepoSupplier,
                                                Predicate<GHRepository> pFilter ) {
    Map<String, Repository> zRV = new TreeMap<>();

    PagedIterable<GHRepository> zRawRepos = pRepoSupplier.get();
    for ( GHRepository zRepo : zRawRepos ) {
      if ( pFilter.test( zRepo ) ) {
        zRV.put( zRepo.getName(), new RepositoryImpl( zRepo, pCreatableFileBuilderFactory ) );
      }
    }
    return zRV;
  }
}
