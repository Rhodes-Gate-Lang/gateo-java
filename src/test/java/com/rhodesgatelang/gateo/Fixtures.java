package com.rhodesgatelang.gateo;

import com.rhodesgatelang.gateo.v3.ComponentInstance;
import com.rhodesgatelang.gateo.v3.GateObject;
import com.rhodesgatelang.gateo.v3.GateType;
import com.rhodesgatelang.gateo.v3.Node;
import com.rhodesgatelang.gateo.v3.Version;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

final class Fixtures {

  private Fixtures() {}

  /** Minimal valid graph: INPUT then OUTPUT feeding it (matches {@code minimal.gateo}). */
  static GateObject minimalOutput() {
    return new GateObject(
        new Version(3, 0),
        List.of(new ComponentInstance("Mod", 0)),
        List.of(
            new Node(
                GateType.INPUT,
                List.of(),
                1,
                0,
                Optional.of("a"),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.OUTPUT,
                List.of(0),
                1,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.empty())));
  }

  /** Slightly richer graph for round-trip coverage. */
  static GateObject minimalAndOr() {
    return new GateObject(
        new Version(3, 1),
        List.of(new ComponentInstance("Demo", 0)),
        List.of(
            new Node(
                GateType.INPUT,
                List.of(),
                1,
                0,
                Optional.of("x"),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.INPUT,
                List.of(),
                1,
                0,
                Optional.of("y"),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.AND,
                List.of(0, 1),
                1,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.OR,
                List.of(2, 0),
                1,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.OUTPUT,
                List.of(3),
                1,
                0,
                Optional.of("z"),
                OptionalLong.empty(),
                OptionalInt.empty())));
  }

  /** LITERAL (8-bit) then SPLIT extracting the MSB-aligned upper nibble. */
  static GateObject splitBus() {
    return new GateObject(
        new Version(3, 0),
        List.of(new ComponentInstance("SliceDemo", 0)),
        List.of(
            new Node(
                GateType.LITERAL,
                List.of(),
                8,
                0,
                Optional.empty(),
                OptionalLong.of(0xABL),
                OptionalInt.empty()),
            new Node(
                GateType.SPLIT,
                List.of(0),
                4,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.of(0))));
  }

  /** Two LITERAL bus halves concatenated via MERGE. */
  static GateObject mergeBus() {
    return new GateObject(
        new Version(3, 0),
        List.of(new ComponentInstance("MergeDemo", 0)),
        List.of(
            new Node(
                GateType.LITERAL,
                List.of(),
                4,
                0,
                Optional.empty(),
                OptionalLong.of(0xAL),
                OptionalInt.empty()),
            new Node(
                GateType.LITERAL,
                List.of(),
                4,
                0,
                Optional.empty(),
                OptionalLong.of(0xBL),
                OptionalInt.empty()),
            new Node(
                GateType.MERGE,
                List.of(0, 1),
                8,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.empty())));
  }

  /** Logical shift left: data INPUT and LITERAL shift amount. */
  static GateObject lslBus() {
    return new GateObject(
        new Version(3, 0),
        List.of(new ComponentInstance("LslDemo", 0)),
        List.of(
            new Node(
                GateType.INPUT,
                List.of(),
                4,
                0,
                Optional.of("d"),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.LITERAL,
                List.of(),
                3,
                0,
                Optional.empty(),
                OptionalLong.of(1L),
                OptionalInt.empty()),
            new Node(
                GateType.LSL,
                List.of(0, 1),
                4,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.empty())));
  }

  /** Logical shift right: data INPUT and LITERAL shift amount. */
  static GateObject lsrBus() {
    return new GateObject(
        new Version(3, 0),
        List.of(new ComponentInstance("LsrDemo", 0)),
        List.of(
            new Node(
                GateType.INPUT,
                List.of(),
                4,
                0,
                Optional.of("d"),
                OptionalLong.empty(),
                OptionalInt.empty()),
            new Node(
                GateType.LITERAL,
                List.of(),
                3,
                0,
                Optional.empty(),
                OptionalLong.of(1L),
                OptionalInt.empty()),
            new Node(
                GateType.LSR,
                List.of(0, 1),
                4,
                0,
                Optional.empty(),
                OptionalLong.empty(),
                OptionalInt.empty())));
  }
}
