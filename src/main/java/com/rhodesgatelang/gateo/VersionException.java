package com.rhodesgatelang.gateo;

/**
 * Thrown when a serialized gate object reports an unsupported {@code version.major} for this
 * library build (expected {@link Gateo#SUPPORTED_SCHEMA_MAJOR}).
 */
public final class VersionException extends RuntimeException {

  public VersionException(String message) {
    super(message);
  }

  public VersionException(String message, Throwable cause) {
    super(message, cause);
  }
}
