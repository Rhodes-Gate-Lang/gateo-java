package com.rhodesgatelang.gateo;

import java.io.IOException;

/** Checked wrapper for failures reading or writing {@code .gateo} files or streams. */
public final class GateoIOException extends IOException {

  public GateoIOException(String message) {
    super(message);
  }

  public GateoIOException(String message, Throwable cause) {
    super(message, cause);
  }
}
