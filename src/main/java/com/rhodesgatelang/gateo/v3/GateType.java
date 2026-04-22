package com.rhodesgatelang.gateo.v3;

import gateo.v3.Gateo;

/** Native gate node kind; mirrors {@code gateo.v3.GateType} without exposing generated protobuf types. */
public enum GateType {
  UNSPECIFIED,
  INPUT,
  OUTPUT,
  AND,
  OR,
  XOR,
  NOT,
  LITERAL,
  SPLIT,
  MERGE,
  LSL,
  LSR;

  public static GateType fromProto(Gateo.GateType proto) {
    return switch (proto) {
      case GATE_TYPE_UNSPECIFIED -> UNSPECIFIED;
      case GATE_TYPE_INPUT -> INPUT;
      case GATE_TYPE_OUTPUT -> OUTPUT;
      case GATE_TYPE_AND -> AND;
      case GATE_TYPE_OR -> OR;
      case GATE_TYPE_XOR -> XOR;
      case GATE_TYPE_NOT -> NOT;
      case GATE_TYPE_LITERAL -> LITERAL;
      case GATE_TYPE_SPLIT -> SPLIT;
      case GATE_TYPE_MERGE -> MERGE;
      case GATE_TYPE_LSL -> LSL;
      case GATE_TYPE_LSR -> LSR;
      case UNRECOGNIZED -> throw new IllegalArgumentException("Unrecognized gate type wire value");
    };
  }

  public Gateo.GateType toProto() {
    return switch (this) {
      case UNSPECIFIED -> Gateo.GateType.GATE_TYPE_UNSPECIFIED;
      case INPUT -> Gateo.GateType.GATE_TYPE_INPUT;
      case OUTPUT -> Gateo.GateType.GATE_TYPE_OUTPUT;
      case AND -> Gateo.GateType.GATE_TYPE_AND;
      case OR -> Gateo.GateType.GATE_TYPE_OR;
      case XOR -> Gateo.GateType.GATE_TYPE_XOR;
      case NOT -> Gateo.GateType.GATE_TYPE_NOT;
      case LITERAL -> Gateo.GateType.GATE_TYPE_LITERAL;
      case SPLIT -> Gateo.GateType.GATE_TYPE_SPLIT;
      case MERGE -> Gateo.GateType.GATE_TYPE_MERGE;
      case LSL -> Gateo.GateType.GATE_TYPE_LSL;
      case LSR -> Gateo.GateType.GATE_TYPE_LSR;
    };
  }
}
