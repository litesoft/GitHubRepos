package org.litesoft.git;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.minimal_ioutils.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CLIsupport {
  private final Logger mLogger;
  private final List<String> mArgs;
  private final Git mGit;
  private final List<String> mOrganizationNames;
  private Service mService;
  private boolean mClear, mRemove; // Flags: false initially

  public CLIsupport( @Nullable Logger pLogger, @Nullable String[] pArgs, @NotNull Git pGit ) {
    mLogger = Logger.deNull( pLogger );
    mArgs = new ArrayList<>( (pArgs != null) ? Arrays.asList( pArgs ) : Collections.emptyList() );
    mGit = NotNull.AssertArgument.namedValue( "Git", pGit );
    mOrganizationNames = mGit.getOrganizationNames();
    mService = new Service( mGit.asRepositoryAccessor() );

    mLogger.log( "Args: " + mArgs );
  }

  public List<String> getRemainingArgs() {
    return mArgs;
  }

  public List<String> getOrganizationNames() {
    return mOrganizationNames;
  }

  public Service getService() {
    return mService;
  }

  public boolean isClear() {
    return mClear;
  }

  public boolean isRemove() {
    return mRemove;
  }

  public void clearRemove() {
    mRemove = false;
  }

  public String optionalArg() {
    return mArgs.isEmpty() ? null : mArgs.remove( 0 );
  }

  public boolean checkArgs( String pFlag ) {
    if ( !mArgs.isEmpty() ) {
      if ( pFlag.equalsIgnoreCase( mArgs.get( 0 ) ) ) {
        mArgs.remove( 0 );
        return true;
      }
    }
    return false;
  }

  public void processArgFlags() {
    if ( checkArgs( "-orgs" ) ) {
      mLogger.log( "\nOrgs (" + mOrganizationNames.size() + "):" );
      for ( String zName : mOrganizationNames ) {
        mLogger.log( "   " + zName );
      }
      System.exit( 0 );
    }

    if ( checkArgs( "-org" ) ) {
      mService = switchToOrg( optionalArg() );
    }

    if ( checkArgs( "-list" ) ) {
      List<String> zRepositoryNames = mService.getRepositoryNames();
      mLogger.log( "\nRepos for '" + mService.getDisplayRef() + "' (" + zRepositoryNames.size() + "):" );
      for ( String zName : zRepositoryNames ) {
        mLogger.log( "   " + zName );
      }
      System.exit( 0 );
    }

    mClear = checkArgs( "-clear" );
    mRemove = checkArgs( "-remove" );
  }

  private Service switchToOrg( @Nullable String pOrg ) {
    pOrg = Significant.ConstrainTo.valueOrNull( pOrg );
    if ( pOrg == null ) {
      exitError( "No Org provided to switch to" );
    }
    if ( !mOrganizationNames.contains( pOrg ) ) {
      exitError( "Switch requested to Org '" + pOrg + "', but not valid, please select from: " + mOrganizationNames );
    }
    Organization zOrganization = mGit.getOrganization( pOrg );
    mLogger.log( "Switched to Org: " + pOrg );
    return new Service( zOrganization );
  }

  private void exitError( String pMessage ) {
    System.err.println( "\n***** " + pMessage + " *****" );
    System.exit( 1 );
  }
}
