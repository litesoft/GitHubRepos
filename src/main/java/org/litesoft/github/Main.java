package org.litesoft.github;

import java.io.IOException;
import java.util.Map;

import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class Main {

  public static void main( String[] args )
          throws IOException {
    GitHub zGitHub = GitHub.connectUsingPassword( "litesoft@gmail.com", "Jan 16, 2003" );

    Map<String, GHOrganization> zMyOrganizations = zGitHub.getMyOrganizations();
    System.out.println( "MyOrganizations: " + zMyOrganizations.keySet() );

    GHOrganization zOrganization = zGitHub.getOrganization( "litesoft-other" );

    Map<String, GHRepository> zRepositories = zOrganization.getRepositories();
    for ( String zKey : zRepositories.keySet() ) {
      System.out.println( "   " + zKey + " | " + zRepositories.get( zKey ).getFullName() );
    }
    GHRepository repo;

    repo = zOrganization.createRepository( "cicdA1" )
            .description( "Continuous Integration and Continuous Delivery for Student A1" )
            .create();
    System.out.println( "Created: " + repo );

//    repo = zOrganization.createRepository( "configA1" )
//            .description( "Configuration for Student A1" )
//            .autoInit( true )
//            .create();
//    System.out.println( "Created: " + repo );

//    repo = zRepositories.get( "configA1" );

//    repo.delete();

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
//    GHRepository repo = github.createRepository(
//            "new-repository","this is my new repository",
//            "http://www.kohsuke.org/",true/*public*/);
//    repo.addCollaborators(github.getUser("abayer"),github.getUser("rtyler"));  }
  }
}
