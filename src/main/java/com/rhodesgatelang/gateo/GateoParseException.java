package com.rhodesgatelang.gateo;

/** Thrown when bytes cannot be interpreted as a {@code gateo.v2.GateObject} protobuf message. */
public final class GateoParseException extends RuntimeException {

  public GateoParseException(String message) {
    super(message);
  }

  public GateoParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
