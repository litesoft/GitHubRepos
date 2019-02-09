package org.litesoft.git;

import org.litesoft.annotations.NotEmpty;

@SuppressWarnings("unused")
public interface Organization extends RepositoryAccessor {
  @NotEmpty
  String getName();

  @NotEmpty
  default String getDisplayRef() {
    return "(Org)" + getName();
  }
}
