package com.rhodesgatelang.gateo;

/** Thrown when a native {@link com.rhodesgatelang.gateo.v3.GateObject} fails basic structural checks. */
public final class GateoValidationException extends RuntimeException {

  public GateoValidationException(String message) {
    super(message);
  }
}
