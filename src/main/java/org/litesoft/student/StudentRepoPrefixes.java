package org.litesoft.student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.litesoft.annotations.Immutable;
import org.litesoft.annotations.NotEmpty;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.git.CreatableFile;

@SuppressWarnings("unused")
public class StudentRepoPrefixes {
  private final Map<String, BuildData> mPrefixes = new TreeMap<>();

  private StudentRepoPrefixes( @NotNull Map<String, BuildData> pPrefixes ) {
    mPrefixes.putAll( pPrefixes );
  }

  @NotNull
  @Immutable
  public List<String> getPrefixes() {
    return new ArrayList<>( mPrefixes.keySet() );
  }

  /**
   * Should only be called with a known good Prefix (from the <code>getPrefixes</code>).
   */
  @NotEmpty
  public String getDescriptionFor( @Significant String pPrefix ) {
    return getBuildDataFor( pPrefix ).mDescription;
  }

  /**
   * Should only be called with a known good Prefix (from the <code>getPrefixes</code>).
   */
  @Nullable
  public CreatableFile getCreatedFileFor( @Significant String pPrefix ) {
    return getBuildDataFor( pPrefix ).mCreatableFile;
  }

  private BuildData getBuildDataFor( String pPrefix ) {
    BuildData zData = mPrefixes.get( Significant.ConstrainTo.valueOrEmpty( pPrefix ) );
    if ( zData != null ) {
      return zData;
    }
    throw new IllegalArgumentException( "Invalid Prefix: " + pPrefix );
  }

  public static class Builder {
    private final Map<String, BuildData> mPrefixes = new HashMap<>();

    public Builder add( @Significant String pPrefix, @NotNull String pDescription, @Nullable CreatableFile pCreatableFile ) {
      mPrefixes.put( Significant.AssertArgument.namedValue( "Prefix", pPrefix ),
                     new BuildData( NotNull.AssertArgument.namedValue( "Description", pDescription ),
                                    pCreatableFile ) );
      return this;
    }

    public Builder add( @Significant String pPrefix, @NotNull String pDescription ) {
      return add( pPrefix, pDescription, null );
    }

    public StudentRepoPrefixes build() {
      return new StudentRepoPrefixes( mPrefixes );
    }
  }

  private static class BuildData {
    String mDescription;
    CreatableFile mCreatableFile;

    private BuildData( String pDescription, CreatableFile pCreatableFile ) {
      mDescription = pDescription;
      mCreatableFile = pCreatableFile;
    }
  }
}
