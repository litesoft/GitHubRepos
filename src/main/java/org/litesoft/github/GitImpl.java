package org.litesoft.github;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.annotations.SignificantOrNull;
import org.litesoft.git.CreatableFile;
import org.litesoft.git.Git;
import org.litesoft.git.Organization;
import org.litesoft.git.Repository;

import static org.litesoft.exceptions.io.IOExceptionalSupplier.Wrapper.process;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GitImpl implements Git {

  private final GitHub mGitHub;
  private final RepoBuilderFactory mRepoBuilderFactory;
  private final CreatableFileBuilderFactory mCreatableFileBuilderFactory;

  public GitImpl( @NotNull GitHub pGitHub, @Nullable RepoBuilderFactory pRepoBuilderFactory,
                  @Nullable CreatableFileBuilderFactory pCreatableFileBuilderFactory ) {
    mGitHub = NotNull.AssertArgument.namedValue( "GitHub", pGitHub );
    mRepoBuilderFactory = (pRepoBuilderFactory != null) ? pRepoBuilderFactory : RepoBuilder::new;
    mCreatableFileBuilderFactory = (pCreatableFileBuilderFactory != null) ? pCreatableFileBuilderFactory : CreatableFileBuilder::new;
  }

  public GitImpl( @NotNull GitHub pGitHub, @Nullable RepoBuilderFactory pRepoBuilderFactory ) {
    this( pGitHub, pRepoBuilderFactory, null );
  }

  public GitImpl( @NotNull GitHub pGitHub, @Nullable CreatableFileBuilderFactory pCreatableFileBuilderFactory ) {
    this( pGitHub, null, pCreatableFileBuilderFactory );
  }

  public GitImpl( @NotNull GitHub pGitHub ) {
    this( pGitHub, null, null );
  }

  public static GitImpl from( @NotNull PasswordCredentials pPasswordCredentials ) {
    NotNull.AssertArgument.namedValue( "PasswordCredentials", pPasswordCredentials );
    GitHubBuilder zBuilder = new GitHubBuilder()
            .withEndpoint( pPasswordCredentials.getEndpoint() )
            .withPassword( pPasswordCredentials.getLogin(), pPasswordCredentials.getPassword() );
    return new GitImpl( process( zBuilder::build ) );
  }

  @Override
  public List<String> getOrganizationNames() {
    List<String> zOrgNames = new ArrayList<>();
    Map<String, GHOrganization> zOrgs = process( mGitHub::getMyOrganizations );
    if ( (zOrgs != null) && !zOrgs.isEmpty() ) {
      zOrgNames.addAll( zOrgs.keySet() );
      Collections.sort( zOrgNames );
    }
    return zOrgNames;
  }

  @Override
  public Organization getOrganization( @SignificantOrNull String pName ) {
    String zName = Significant.ConstrainTo.valueOrNull( pName );
    GHOrganization zOrg = (zName == null) ? null :
                          process( () -> mGitHub.getOrganization( zName ) );
    return (zOrg == null) ? null : new OrganizationImpl( zOrg, zName, mRepoBuilderFactory, mCreatableFileBuilderFactory );
  }

  @Override
  public Map<String, Repository> getRepositories() {
    GHMyself zMyself = process( mGitHub::getMyself );
    String zFullNamePrefix = zMyself.getLogin() + "/";
    return RepositoryImpl.map( mCreatableFileBuilderFactory,
                               zMyself::listRepositories,
                               pRepository -> pRepository.getFullName().startsWith( zFullNamePrefix ) );
  }

  @Override
  public String getLogin() {
    GHMyself zMyself = process( mGitHub::getMyself );
    return (zMyself == null) ? null : zMyself.getLogin();
  }

  @Override
  public String toString() {
    return "GitHub(" + mGitHub.getApiUrl() + "):" + getLogin();
  }

  @Override
  public Repository.Builder createRepositoryOutsideOfOrganization( String pName ) {
    String zName = Significant.AssertArgument.namedValue( "Name", pName );
    return mRepoBuilderFactory.create( mGitHub.createRepository( zName ), mCreatableFileBuilderFactory );
  }

  @Override
  public CreatableFile.Builder createFileBuilder( @Significant String pFilePath ) {
    return mCreatableFileBuilderFactory.create( Significant.AssertArgument.namedValue( "FilePath", pFilePath ) );
  }

  protected static class RepoBuilder implements Repository.Builder {
    private final GHCreateRepositoryBuilder mBuilder;
    private final CreatableFileBuilderFactory mCreatableFileBuilderFactory;
    private boolean mDescriptionSet;

    protected RepoBuilder( GHCreateRepositoryBuilder pBuilder, CreatableFileBuilderFactory pCreatableFileBuilderFactory ) {
      mBuilder = pBuilder;
      mCreatableFileBuilderFactory = pCreatableFileBuilderFactory;
    }

    @Override
    public Repository.Builder description( String pDescription ) {
      if ( pDescription != null ) {
        pDescription = pDescription.trim();
        if ( !pDescription.isEmpty() ) {
          mBuilder.description( pDescription );
          mDescriptionSet = true;
        }
      }
      return this;
    }

    @Override
    public Repository.Builder initWithDescriptionAsReadMe() {
      if ( !mDescriptionSet ) {
        throw new IllegalStateException( "Description not set before attempt to use it" );
      }
      mBuilder.autoInit( true );
      return this;
    }

    @Override
    public Repository create() {
      GHRepository zRepo = process( mBuilder::create );
      return new RepositoryImpl( zRepo, mCreatableFileBuilderFactory );
    }
  }

  protected static class CreatableFileBuilder implements CreatableFile.Builder {
    private final String mFilePath;
    private String mOptionalBranch;
    private String mTextFileContents;
    private byte[] mBinaryFileContents;

    @SuppressWarnings("WeakerAccess")
    protected CreatableFileBuilder( String pFilePath ) {
      mFilePath = pFilePath; // TODO: Significant?
    }

    @Override
    public CreatableFile.Builder optionalBranch( String pBranch ) {
      mOptionalBranch = pBranch;  // TODO: SignificantOrNull?
      return this;
    }

    @Override
    public CreatableFile.Builder textFileContents( String pContentsUTF8 ) {
      mTextFileContents = pContentsUTF8;
      return this;
    }

    @Override
    public CreatableFile.Builder binaryFileContents( byte[] pContents ) {
      mBinaryFileContents = pContents;
      return this;
    }

    @Override
    public CreatableFile build( String pCommitMessage )
            throws IllegalStateException {
      pCommitMessage = Objects.requireNonNull( pCommitMessage, "CommitMessage" );  // TODO: Significant
      boolean zHasBinaryContent = (mBinaryFileContents != null);
      boolean zHasTextContent = (mTextFileContents != null);
      if ( zHasBinaryContent && zHasTextContent ) {
        throw new IllegalStateException( "CreateFile requires either text or binary content, but not both" );
      }
      if ( !zHasBinaryContent && !zHasTextContent ) {
        throw new IllegalStateException( "CreateFile requires content (either text or binary), but none was provided" );
      }
      return new CreatableFileImpl( mFilePath, pCommitMessage, mOptionalBranch, mTextFileContents, mBinaryFileContents );
    }
  }

  protected static class CreatableFileImpl implements CreatableFile {
    private final String mFilePath;
    private final String mCommitMessage;
    private final String mOptionalBranch;
    private final String mTextFileContents;
    private final byte[] mBinaryFileContents;

    @SuppressWarnings("WeakerAccess")
    protected CreatableFileImpl( String pFilePath, String pCommitMessage, String pOptionalBranch, String pTextFileContents, byte[] pBinaryFileContents ) {
      mFilePath = pFilePath;
      mCommitMessage = pCommitMessage;
      mOptionalBranch = pOptionalBranch;
      mTextFileContents = pTextFileContents;
      mBinaryFileContents = pBinaryFileContents;
    }

    @Override
    public String getFilePath() {
      return mFilePath;
    }

    @Override
    public String getCommitMessage() {
      return mCommitMessage;
    }

    @Override
    public String getOptionalBranch() {
      return mOptionalBranch;
    }

    @Override
    public String getTextFileContents() {
      return mTextFileContents;
    }

    @Override
    public byte[] getBinaryFileContents() {
      return mBinaryFileContents;
    }
  }
}
