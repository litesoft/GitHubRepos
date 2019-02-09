package org.litesoft.github;

import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.annotations.expectations.IllegalArgument;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PasswordCredentials {
  public static final String PROTOCOL = "https://";
  public static final String GITHUB_BASE_URI = "github.com";

  private static final String OTHER_PROTOCOL = "http://";
  private static final String[] ENTERPRISE_TRAILERS = {"/api", "/v3"};
  private static final String NON_ENTERPRISE_PREFIX = "api.";

  private final String mEndpoint;
  private final String mLogin;
  private final String mPassword;

  public PasswordCredentials( @Nullable String pEndpoint, @Significant String pLogin, @Significant String pPassword ) {
    mEndpoint = new EndpointNormalizer( "Endpoint", pEndpoint ).validateAndMorph();
    mLogin = Significant.AssertArgument.namedValue( "Login", pLogin );
    mPassword = Significant.AssertArgument.namedValue( "Password", pPassword );
  }

  public PasswordCredentials( @Significant String pLogin, @Significant String pPassword ) {
    this( null, pLogin, pPassword );
  }

  @Significant
  public String getEndpoint() {
    return mEndpoint;
  }

  @Significant
  public String getLogin() {
    return mLogin;
  }

  @Significant
  public String getPassword() {
    return mPassword;
  }

  @Override
  public String toString() {
    return "PasswordCredentials( \""
           + getEndpoint() + "\", \""
           + getLogin() + "\", \""
           + getPassword() + "\" )";
  }

  static class EndpointNormalizer {
    private final String mName;
    private final String mOriginalEndpoint;
    private String mEndpoint;

    EndpointNormalizer( String pName, @Nullable String pEndpoint ) {
      mName = pName;
      mEndpoint = mOriginalEndpoint = Significant.ConstrainTo.valueOr( pEndpoint, GITHUB_BASE_URI );
      clean();
    }

    String getEndpoint() {
      return mEndpoint;
    }

    private void clean() {
      mEndpoint = mEndpoint.toLowerCase().replace( '\\', '/' );
      cleanFront();
      cleanTail();
    }

    private void cleanFront() {
      removeFromFront( PROTOCOL );
      removeFromFront( OTHER_PROTOCOL );
      removeFromFront( NON_ENTERPRISE_PREFIX );
    }

    private void cleanTail() {
      String[] zTrailers = ENTERPRISE_TRAILERS;
      int zOffsetTrailer = zTrailers.length - 1;
      while ( true ) {
        removeAllFromEnd( '/' );
        if ( zOffsetTrailer < 0 ) {
          return;
        }
        removeFromEnd( zTrailers[zOffsetTrailer--] );
      }
    }

    private void removeFromFront( String pString ) {
      if ( mEndpoint.startsWith( pString ) ) {
        mEndpoint = mEndpoint.substring( pString.length() );
      }
    }

    @SuppressWarnings("SameParameterValue")
    private void removeAllFromEnd( char pCheckFor ) {
      for ( int zLastCharAt = mEndpoint.length() - 1; 0 <= zLastCharAt; zLastCharAt-- ) {
        if ( pCheckFor != mEndpoint.charAt( zLastCharAt ) ) {
          return;
        }
        mEndpoint = mEndpoint.substring( 0, zLastCharAt );
      }
    }

    private void removeFromEnd( String pCheckFor ) {
      if ( mEndpoint.endsWith( pCheckFor ) ) {
        mEndpoint = mEndpoint.substring( 0, mEndpoint.length() - pCheckFor.length() );
      }
    }

    String validateAndMorph() {
      validate();
      StringBuilder sb = new StringBuilder().append( PROTOCOL );
      if ( GITHUB_BASE_URI.equals( mEndpoint ) ) {
        sb.append( NON_ENTERPRISE_PREFIX ).append( mEndpoint );
      } else {
        sb.append( mEndpoint );
        for ( String zTrailer : ENTERPRISE_TRAILERS ) {
          sb.append( zTrailer );
        }
      }
      return sb.toString();
    }

    private void validate() {
      for ( int i = 0; i < mEndpoint.length(); i++ ) {
        char c = mEndpoint.charAt( i );
        if ( (c <= ' ') || (c == '/') || Character.isWhitespace( c ) ) {
          throw IllegalArgument.expectationUnmet( mName, "unacceptable (passed value \""
                                                         + mOriginalEndpoint + "\") due to character '"
                                                         + c + "' at offset "
                                                         + i + " in (normalized) value \""
                                                         + mEndpoint + "\"" );
        }
      }
    }
  }
}
