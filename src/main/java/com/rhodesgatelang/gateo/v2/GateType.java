package com.rhodesgatelang.gateo.v2;

import gateo.v2.Gateo;

/** Native gate node kind; mirrors {@code gateo.v2.GateType} without exposing generated protobuf types. */
public enum GateType {
  UNSPECIFIED,
  INPUT,
  OUTPUT,
  AND,
  OR,
  XOR,
  NOT,
  LITERAL;

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
    };
  }
}
