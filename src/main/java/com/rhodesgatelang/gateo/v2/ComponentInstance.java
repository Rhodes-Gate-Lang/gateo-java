package com.rhodesgatelang.gateo.v2;

import java.util.Objects;

/**
 * One node in the component instance tree. Index {@code 0} is always the synthetic root; its {@link
 * #parent()} is {@code 0} (self).
 */
public record ComponentInstance(String name, int parent) {
  public ComponentInstance {
    Objects.requireNonNull(name, "name");
    if (parent < 0) {
      throw new IllegalArgumentException("parent must be non-negative");
    }
  }
}
