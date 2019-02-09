package org.litesoft.git;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.annotations.SignificantOrNull;

@SuppressWarnings("unused")
public interface CreatableFile {
  /**
   * Significant FilePath.
   */
  @Significant
  String getFilePath();

  /**
   * Significant Path.
   */
  @Significant
  String getCommitMessage();

  /**
   * Optional Branch (Significant if not <code>null</code>).
   */
  @SignificantOrNull
  String getOptionalBranch();

  /**
   * TextFileContents or <code>null</code> (if null then BinaryFileContents can NOT be <code>null</code>).
   */
  @Nullable
  String getTextFileContents();

  /**
   * BinaryFileContents or <code>null</code> (if null then TextFileContents can NOT be <code>null</code>).
   */
  @Nullable
  byte[] getBinaryFileContents();

  interface Builder {
    /**
     * Add (or clear - if param is null) a branch.
     *
     * @param pBranch must be significant or null (pr not set) when <code>create</code> is called.
     *
     * @return this for fluent support
     */
    Builder optionalBranch( @SignificantOrNull String pBranch );

    /**
     * Add contents (as a UTF8 based text file).
     *
     * @param pContentsUTF8 empty string supported
     *
     * @return this for fluent support
     */
    Builder textFileContents( @Nullable String pContentsUTF8 );

    /**
     * Add contents (as a binary array of bytes).
     *
     * @param pContents empty array supported
     *
     * @return this for fluent support
     */
    Builder binaryFileContents( @Nullable byte[] pContents );

    /**
     * Validate the parameters and return an Immutable CreatedableFile.
     * <p>
     * Either <code>textFileContents</code> OR <code>binaryFileContents</code> (but not both) must have been set!
     *
     * @param pCommitMessage must be significant!
     *
     * @return A Validated <code>CreatableFile</code> (Validated does not mean that it is creatable, but that the minimum requirement to create have been met)
     *
     * @throws IllegalStateException should there be a requirement not met
     */
    @NotNull
    CreatableFile build( @Significant String pCommitMessage )
            throws IllegalStateException;
  }
}
