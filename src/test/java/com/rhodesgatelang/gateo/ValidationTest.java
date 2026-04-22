package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rhodesgatelang.gateo.v3.ComponentInstance;
import com.rhodesgatelang.gateo.v3.GateObject;
import com.rhodesgatelang.gateo.v3.GateObjectValidator;
import com.rhodesgatelang.gateo.v3.GateType;
import com.rhodesgatelang.gateo.v3.Node;
import com.rhodesgatelang.gateo.v3.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

class ValidationTest {

  @Test
  void validateBasicRejectsEmptyComponents() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void validateBasicRejectsOutOfRangeOperand() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("Mod", 0)),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty()),
                new Node(
                    GateType.OUTPUT,
                    List.of(99),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void validateBasicRejectsSelfOperand() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("Mod", 0)),
            List.of(
                new Node(
                    GateType.OUTPUT,
                    List.of(0),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void writeAndReadAllowLenientInputOperands() throws Exception {
    GateObject graph =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("Mod", 0)),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty()),
                new Node(
                    GateType.INPUT,
                    List.of(0),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    Path file = Files.createTempFile("lenient", ".gateo");
    try {
      Gateo.write(file, graph);
      assertEquals(graph, Gateo.read(file));
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  void splitRejectsRangePastInputWidth() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("M", 0)),
            List.of(
                new Node(
                    GateType.LITERAL,
                    List.of(),
                    4,
                    0,
                    Optional.empty(),
                    OptionalLong.of(0L),
                    OptionalInt.empty()),
                new Node(
                    GateType.SPLIT,
                    List.of(0),
                    4,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.of(2))));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void mergeRejectsArityBelowTwo() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("M", 0)),
            List.of(
                new Node(
                    GateType.LITERAL,
                    List.of(),
                    8,
                    0,
                    Optional.empty(),
                    OptionalLong.of(1L),
                    OptionalInt.empty()),
                new Node(
                    GateType.MERGE,
                    List.of(0),
                    8,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void mergeRejectsWidthMismatch() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("M", 0)),
            List.of(
                new Node(
                    GateType.LITERAL,
                    List.of(),
                    4,
                    0,
                    Optional.empty(),
                    OptionalLong.of(1L),
                    OptionalInt.empty()),
                new Node(
                    GateType.LITERAL,
                    List.of(),
                    4,
                    0,
                    Optional.empty(),
                    OptionalLong.of(2L),
                    OptionalInt.empty()),
                new Node(
                    GateType.MERGE,
                    List.of(0, 1),
                    7,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void lslRejectsNonLiteralShiftAmount() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("M", 0)),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(),
                    4,
                    0,
                    Optional.of("a"),
                    OptionalLong.empty(),
                    OptionalInt.empty()),
                new Node(
                    GateType.INPUT,
                    List.of(),
                    3,
                    0,
                    Optional.of("b"),
                    OptionalLong.empty(),
                    OptionalInt.empty()),
                new Node(
                    GateType.LSL,
                    List.of(0, 1),
                    4,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void lsrRejectsNonLiteralShiftAmount() {
    GateObject bad =
        new GateObject(
            new Version(3, 0),
            List.of(new ComponentInstance("M", 0)),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(),
                    4,
                    0,
                    Optional.of("a"),
                    OptionalLong.empty(),
                    OptionalInt.empty()),
                new Node(
                    GateType.INPUT,
                    List.of(),
                    3,
                    0,
                    Optional.of("b"),
                    OptionalLong.empty(),
                    OptionalInt.empty()),
                new Node(
                    GateType.LSR,
                    List.of(0, 1),
                    4,
                    0,
                    Optional.empty(),
                    OptionalLong.empty(),
                    OptionalInt.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }
}
