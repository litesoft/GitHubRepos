package org.litesoft.training.seat.cicdandconf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
          throws Exception {
    PasswordCredentials zCredentials = PasswordCredentialsPropertiesLoader.load( "credentials.properties" );

    Logger zLogger = Logger.createFrom( System.out );

    zLogger.log( "GitHubRepos V1.1" );
//    zLogger.log( "GitHubRepos V1.0 using: " + zCredentials );

    Git zGit = GitImpl.from( zCredentials );

    CLIsupport zCLIsupport = new CLIsupport( zLogger, args, zGit );

    zCLIsupport.processArgFlags();

    boolean zLegacy = zCLIsupport.checkArgs( "-legacy" );

    Seats zSeats = generateSeats( zCLIsupport.getRemainingArgs() );

    if ( zCLIsupport.isSeatsOnly() ) {
      if ( zSeats.isEmpty() ) {
        throw new IllegalArgumentException( "'-seatsOnly' specified without any Seats!" );
      }
      zLogger.log( "Seats:" );
      for ( String zSeat : zSeats.getSeats() ) {
        String zGenerated = zCLIsupport.getSeatsOnlyPrefix() + zSeat + zCLIsupport.getSeatsOnlySuffix();
        if ( !zCLIsupport.isExecute() ) {
          zLogger.log( "    " + zGenerated );
        } else {
          zLogger.log( "    Executing: " + zGenerated );
          execute( zCLIsupport, zGenerated );
        }
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
//    repo.addCollaborators(github.getUser("abayer"),github.getUser("rtyler"));  }
//  }

  private static void execute( CLIsupport pCLIsupport, String pGenerated )
          throws IOException, InterruptedException {
    if ( pCLIsupport.isDryRun() ) {
      pGenerated = "echo DryRun: " + pGenerated;
    }
    Process zProcess = new ProcessBuilder( toListSplitSP( pGenerated ) ).inheritIO().start();
    for ( int zSecs = 30; zSecs > 0; zSecs-- ) {
      boolean zFinished = zProcess.waitFor( 1, TimeUnit.SECONDS );
      if ( zFinished ) {
        int zExitValue = zProcess.exitValue();
        if ( zExitValue != 0 ) {
          pCLIsupport.exitError( zExitValue, "ExitValue (" + zExitValue + "): " + pGenerated );
        }
        return;
      }
    }
    pCLIsupport.exitError( "Timeout (30secs): " + pGenerated );
  }

  private static List<String> toListSplitSP( String pGenerated ) {
    List<String> zStrings = new ArrayList<>();
    pGenerated = pGenerated.trim();
    for ( int zAt; -1 != (zAt = pGenerated.indexOf( ' ' )); pGenerated = pGenerated.substring( zAt + 1 ).trim() ) {
      zStrings.add( pGenerated.substring( 0, zAt ) );
    }
    zStrings.add( pGenerated );
    return zStrings;
  }
}
