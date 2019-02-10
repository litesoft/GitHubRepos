package org.litesoft.training.kaiser;

import java.io.IOException;
import java.util.List;

import org.litesoft.git.CLIsupport;
import org.litesoft.git.CreatableFile;
import org.litesoft.git.Git;
import org.litesoft.git.Service;
import org.litesoft.github.GitImpl;
import org.litesoft.github.PasswordCredentials;
import org.litesoft.github.PasswordCredentialsPropertiesLoader;
import org.litesoft.minimal_ioutils.Logger;
import org.litesoft.student.LegacyStudentFilter;
import org.litesoft.student.Seats;
import org.litesoft.student.StudentRepoFilter;
import org.litesoft.student.StudentRepoPrefixes;

public class GitHubRepos {
  private static final String CONFIGURATION = "config";
  private static final String CICD = "cicd";

  public static void main( String[] args )
          throws IOException {
    PasswordCredentials zCredentials = PasswordCredentialsPropertiesLoader.load( "credentials.properties" );

    Logger zLogger = Logger.createFrom( System.out );

    zLogger.log( "GitHubRepos V1.0 using: " + zCredentials );

    Git zGit = GitImpl.from( zCredentials );

    CLIsupport zCLIsupport = new CLIsupport( zLogger, args, zGit );

    zCLIsupport.processArgFlags();

    boolean zLegacy = zCLIsupport.checkArgs( "-legacy" );

    Seats zSeats = generateSeats( zCLIsupport.getRemainingArgs() );

    if (zCLIsupport.isSeatsOnly()) {
      if ( zSeats.isEmpty() ) {
        throw new IllegalArgumentException( "'-seatsOnly' specified without any Seats!" );
      }
      zLogger.log( "Seats:" );
      for ( String zSeat : zSeats.getSeats() ) {
        zLogger.log( "    " + zCLIsupport.getSeatsOnlyPrefix() + zSeat );
      }
      System.exit( 0 );
    }

    Service zService = zCLIsupport.getService();

    if ( zLegacy ) {
      zCLIsupport.clearRemove();
      zLogger.log( "Removing all Legacy Repos..." );
      zService.removeRepositories( zLogger, new LegacyStudentFilter() );
    }

    CreatableFile zCreatableFile = createConfigurationFile( zGit );

    StudentRepoPrefixes zRepoPrefixes = new StudentRepoPrefixes.Builder()
            .add( CONFIGURATION, "Configuration for Student ", zCreatableFile )
            .add( CICD, "Continuous Integration and Continuous Delivery for Student " )
            .build();

    if ( zCLIsupport.isClear() ) {
      if ( zCLIsupport.isRemove() ) {
        throw new IllegalArgumentException( "'-remove' may not be combined with '-clear', unless '-legacy' is also specified!" );
      }
      zLogger.log( "Removing all Seat based Repos..." );
      zService.removeRepositories( zLogger, new StudentRepoFilter( zRepoPrefixes.getPrefixes(),
                                                                   Seats::isValidSeatID ) );
    }

    if ( zCLIsupport.isRemove() ) {
      if ( zSeats.isEmpty() ) {
        throw new IllegalArgumentException( "'-remove' may NOT be specified without any Seats!" );
      }
      zLogger.log( "Removing Specified Seat(s) based Repos..." );
      zService.removeRepositories( zLogger, new StudentRepoFilter( zRepoPrefixes.getPrefixes(),
                                                                   zSeats::hasSeat ) );
    } else if ( !zSeats.isEmpty() ) {
      zLogger.log( "Creating (" + zSeats.count() + ") Seat based Repos..." );
      createStudentRepos( zLogger, zService, zSeats, zRepoPrefixes );
    }
  }

  private static Seats generateSeats( List<String> pArgs ) {
    Seats.Builder zBuilder = new Seats.Builder();
    if ( !pArgs.isEmpty() ) {
      do {
        zBuilder.add( pArgs.remove( 0 ) );
      } while ( !pArgs.isEmpty() );
    }
    return zBuilder.build();
  }

  private static void createStudentRepos( Logger pLogger, Service pService, Seats pSeats, StudentRepoPrefixes pPrefixes ) {
    List<String> zPrefixes = pPrefixes.getPrefixes();
    for ( String zSeat : pSeats.getSeats() ) {
      for ( String zPrefix : zPrefixes ) {
        String zRepoName = zPrefix + zSeat;
        pService.createRepo( pLogger, zRepoName, pPrefixes.getDescriptionFor( zPrefix ) + zSeat,
                             pPrefixes.getCreatedFileFor( zPrefix ) );
      }
    }
  }

  private static CreatableFile createConfigurationFile( Git pGit ) {
    return pGit
            .createFileBuilder( "config/dev/application-dev.yml" )
            .textFileContents(
                    "APP_CONFIG:\n" +
                    "  systemPwd: \"!di123$?\"\n" +
                    "logging:\n" +
                    "  level:\n" +
                    "    com.example: ERROR\n" )
            .build( "With application-dev.yml" );
  }

//  @SuppressWarnings("unused")
//  private void testCode( Git zGit ) {
//    System.out.println( "Mine: " + zRepositories.size() );
//
//    GitHub zGitHub = new GitHubBuilder()
//            .withEndpoint( zCredentials.getEndpoint() )
//            .withPassword( zCredentials.getLogin(), zCredentials.getPassword() )
//            .build();
//
//    GHMyself zMyself = zGitHub.getMyself();
//    System.out.println( "Myself: " + zMyself.getLogin() + "|" + zMyself.getEmails() );
//
//
//    Map<String, Set<GHTeam>> zMyTeams = zGitHub.getMyTeams();
//    System.out.println( "MyTeams: " + zMyTeams.keySet() );
//
//    Map<String, GHRepository> zLitesoftRepos = zMyself.getAllRepositories();
//    for ( String zKey : zLitesoftRepos.keySet() ) {
//      System.out.println( "   " + zKey + " | " + zLitesoftRepos.get( zKey ).getFullName() );
//    }
//
//    System.out.println( "All: " + zLitesoftRepos.size() );
//
//    Git zGit = new GitImpl( zGitHub );
//
//    System.out.println( "Mine: " + zGit.getRepositories().size() );
//
//    System.out.println( "Other's: " + zGit.getOrganization( "litesoft-other" ).getRepositories().size() );
//
//    List<String> zMyOrganizations = zGit.getOrganizationNames();
//    System.out.println( "MyOrganizations: " + zMyOrganizations );
//
//    Organization zOrganization = zGit.getOrganization( "litesoft-other" );
//
//    Map<String, Repository> zRepositories = zOrganization.getRepositories();
//    for ( String zKey : zRepositories.keySet() ) {
//      System.out.println( "   " + zKey + " | " + zRepositories.get( zKey ).getFullName() );
//    }
//
//    Repository repo;
//
//    repo = zOrganization.createRepository( "cicdA4" )
//            .description( "Continuous Integration and Continuous Delivery for Student A4" )
//            .create();
//    System.out.println( "Created: " + repo );
//
//    repo = zOrganization.createRepository( "configA4" )
//            .description( "Configuration for Student A4" )
//            .initWithDescriptionAsReadMe()
//            .create();
//    System.out.println( "Created: " + repo );
//
//    CreatableFile zCreatableFile = repo
//            .createFileBuilder( "config/dev/application-dev.yml" )
//            .textFileContents(
//                    "APP_CONFIG:\n" +
//                    "  systemPwd: \"!di123$?\"\n" +
//                    "logging:\n" +
//                    "  level:\n" +
//                    "    com.example: ERROR\n" )
//            .build( "With application-dev.yml" );
//    repo.createFile( zCreatableFile );
//
//    repo = zRepositories.get( "configA1" );
//
//    repo.delete();
//
//    repo.addCollaborators(github.getUser("abayer"),github.getUser("rtyler"));  }
//  }
}
