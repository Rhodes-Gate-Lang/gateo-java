package com.rhodesgatelang.gateo.v3;

import java.util.List;
import java.util.Objects;

/**
 * Immutable view of a compiled gate graph and its component instance tree.
 *
 * <p>Unknown protobuf fields are not represented here; round-tripping through this model may drop
 * data that exists only on the wire.
 */
public record GateObject(Version version, List<ComponentInstance> components, List<Node> nodes) {
  public GateObject {
    Objects.requireNonNull(version, "version");
    Objects.requireNonNull(components, "components");
    Objects.requireNonNull(nodes, "nodes");
    components = List.copyOf(components);
    nodes = List.copyOf(nodes);
  }
}
