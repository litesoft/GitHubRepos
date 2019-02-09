package org.litesoft.github;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.litesoft.annotations.NotEmpty;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Significant;
import org.litesoft.exceptions.io.IOExceptionConverter;
import org.litesoft.minimal_ioutils.Closeables;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PasswordCredentialsPropertiesLoader {
  public static final String ENDPOINT = "endpoint";
  public static final String LOGIN = "login";
  public static final String PASSWORD = "password";

  public static PasswordCredentials load( @Significant String pSource, @NotNull Properties pProperties )
          throws IOException {
    pSource = Significant.AssertArgument.namedValue( "Source", pSource );
    NotNull.AssertArgument.namedValue( "Properties", pProperties );

    try {
      return new PasswordCredentials( pProperties.getProperty( ENDPOINT, PasswordCredentials.GITHUB_BASE_URI ),
                                      pProperties.getProperty( LOGIN ),
                                      pProperties.getProperty( PASSWORD ) );
    }
    catch ( Exception e ) {
      throw IOExceptionConverter.withSource( pSource, e );
    }
  }

  /**
   * Load the PasswordCredentials from the InputStream provided (which is closed).
   */
  public static PasswordCredentials load( @Significant String pSource, @NotNull InputStream pInputStream )
          throws IOException {
    NotNull.AssertArgument.namedValue( "InputStream", pInputStream );
    try {
      pSource = Significant.AssertArgument.namedValue( "Source", pSource );
      try {
        return load( pSource, createPropertiesFrom( pInputStream ) );
      }
      catch ( Exception e ) {
        throw IOExceptionConverter.withSource( pSource, e );
      }
    }
    finally {
      Closeables.closeQuietly( pInputStream );
    }
  }

  public static PasswordCredentials load( @NotNull File pFileSource )
          throws IOException {
    NotNull.AssertArgument.namedValue( "FileSource", pFileSource );
    String zSource = pFileSource.getPath();
    try {
      pFileSource = pFileSource.getCanonicalFile();
      zSource = pFileSource.getPath();
      return load( zSource, new FileInputStream( pFileSource ) );
    }
    catch ( Exception e ) {
      throw IOExceptionConverter.withSource( zSource, e );
    }
  }

  public static PasswordCredentials load( @NotEmpty String pFilePath )
          throws IOException {
    return load( new File( NotEmpty.AssertArgument.namedValue( "FilePath", pFilePath ) ) );
  }

  /**
   * Don't close the <code>InputStream</code>, handled by caller!
   */
  private static Properties createPropertiesFrom( InputStream pInputStream )
          throws IOException {
    Properties zProperties = new Properties();
    zProperties.load( pInputStream );
    return zProperties;
  }
}
