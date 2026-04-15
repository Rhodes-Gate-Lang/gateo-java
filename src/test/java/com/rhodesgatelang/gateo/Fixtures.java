package com.rhodesgatelang.gateo;

import com.rhodesgatelang.gateo.v2.ComponentInstance;
import com.rhodesgatelang.gateo.v2.GateObject;
import com.rhodesgatelang.gateo.v2.GateType;
import com.rhodesgatelang.gateo.v2.Node;
import com.rhodesgatelang.gateo.v2.Version;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

final class Fixtures {

  private Fixtures() {}

  /** Minimal valid graph: INPUT then OUTPUT feeding it (matches {@code minimal.gateo}). */
  static GateObject minimalOutput() {
    return new GateObject(
        new Version(2, 0),
        List.of(new ComponentInstance("Mod", 0)),
        List.of(
            new Node(
                GateType.INPUT,
                List.of(),
                1,
                0,
                Optional.of("a"),
                OptionalLong.empty()),
            new Node(
                GateType.OUTPUT,
                List.of(0),
                1,
                0,
                Optional.empty(),
                OptionalLong.empty())));
  }

  /** Slightly richer graph for round-trip coverage. */
  static GateObject minimalAndOr() {
    return new GateObject(
        new Version(2, 1),
        List.of(new ComponentInstance("Demo", 0)),
        List.of(
            new Node(GateType.INPUT, List.of(), 1, 0, Optional.of("x"), OptionalLong.empty()),
            new Node(GateType.INPUT, List.of(), 1, 0, Optional.of("y"), OptionalLong.empty()),
            new Node(GateType.AND, List.of(0, 1), 1, 0, Optional.empty(), OptionalLong.empty()),
            new Node(GateType.OR, List.of(2, 0), 1, 0, Optional.empty(), OptionalLong.empty()),
            new Node(GateType.OUTPUT, List.of(3), 1, 0, Optional.of("z"), OptionalLong.empty())));
  }
}
