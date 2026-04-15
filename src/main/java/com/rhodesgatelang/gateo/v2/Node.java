package com.rhodesgatelang.gateo.v2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

/** One gate node in topological order (all operand indices precede this node). */
public record Node(
    GateType type,
    List<Integer> inputs,
    int width,
    int parent,
    Optional<String> name,
    OptionalLong literalValue) {
  public Node {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(inputs, "inputs");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(literalValue, "literalValue");
    inputs = List.copyOf(inputs);
    if (width < 0) {
      throw new IllegalArgumentException("width must be non-negative");
    }
    if (parent < 0) {
      throw new IllegalArgumentException("parent must be non-negative");
    }
  }
}
