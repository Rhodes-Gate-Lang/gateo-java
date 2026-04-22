package com.rhodesgatelang.gateo.v3;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * One gate node in topological order (all operand indices precede this node).
 *
 * <p>{@link #value} holds the LITERAL constant when {@link #type} is {@link GateType#LITERAL}, and
 * may also record simulator-computed state for any node type on the wire.
 */
public record Node(
    GateType type,
    List<Integer> inputs,
    int width,
    int parent,
    Optional<String> name,
    OptionalLong value,
    OptionalInt splitLo) {
  public Node {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(inputs, "inputs");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(value, "value");
    Objects.requireNonNull(splitLo, "splitLo");
    inputs = List.copyOf(inputs);
    if (width < 1) {
      throw new IllegalArgumentException("width must be at least 1");
    }
    if (parent < 0) {
      throw new IllegalArgumentException("parent must be non-negative");
    }
  }
}
