package com.rhodesgatelang.gateo.v2;

import com.rhodesgatelang.gateo.GateoValidationException;
import java.util.List;

/** Basic structural validation for native {@link GateObject} graphs. */
public final class GateObjectValidator {

  private GateObjectValidator() {}

  /**
   * Validates ordering, indexing, and minimal arity constraints implied by {@code gateo.v2}.
   *
   * @throws GateoValidationException when the object is structurally invalid
   */
  public static void validateBasic(GateObject object) {
    Version version = object.version();
    if (version.major() < 0 || version.minor() < 0) {
      throw new GateoValidationException("version fields must be non-negative");
    }

    List<ComponentInstance> components = object.components();
    if (components.isEmpty()) {
      throw new GateoValidationException("components must be non-empty");
    }
    if (components.get(0).parent() != 0) {
      throw new GateoValidationException("root component (index 0) must have parent == 0");
    }
    for (int i = 0; i < components.size(); i++) {
      ComponentInstance c = components.get(i);
      int p = c.parent();
      if (p < 0 || p >= components.size()) {
        throw new GateoValidationException(
            "component " + i + " has parent index out of range: " + p);
      }
      if (i > 0 && p == i) {
        throw new GateoValidationException("non-root component " + i + " cannot be its own parent");
      }
    }

    List<Node> nodes = object.nodes();
    if (nodes.isEmpty()) {
      throw new GateoValidationException("nodes must be non-empty");
    }

    for (int i = 0; i < nodes.size(); i++) {
      Node node = nodes.get(i);
      if (node.type() == GateType.UNSPECIFIED) {
        throw new GateoValidationException("node " + i + " has UNSPECIFIED gate type");
      }
      if (node.width() <= 0) {
        throw new GateoValidationException("node " + i + " must have width > 0");
      }
      int parent = node.parent();
      if (parent < 0 || parent >= components.size()) {
        throw new GateoValidationException("node " + i + " has invalid component parent " + parent);
      }

      List<Integer> inputs = node.inputs();
      for (int inIdx = 0; inIdx < inputs.size(); inIdx++) {
        int operand = inputs.get(inIdx);
        if (operand < 0 || operand >= i) {
          throw new GateoValidationException(
              "node "
                  + i
                  + " input "
                  + inIdx
                  + " must reference an earlier node index (< "
                  + i
                  + "), got "
                  + operand);
        }
      }

      validateArity(node.type(), inputs.size(), i);

      if (node.type() == GateType.LITERAL && node.literalValue().isEmpty()) {
        throw new GateoValidationException("LITERAL node " + i + " must provide literalValue");
      }
    }
  }

  private static void validateArity(GateType type, int inputCount, int nodeIndex) {
    switch (type) {
      case INPUT -> {
        if (inputCount != 0) {
          throw new GateoValidationException(
              "INPUT node " + nodeIndex + " must have zero inputs, got " + inputCount);
        }
      }
      case LITERAL -> {
        if (inputCount != 0) {
          throw new GateoValidationException(
              "LITERAL node " + nodeIndex + " must have zero inputs, got " + inputCount);
        }
      }
      case OUTPUT -> {
        if (inputCount < 1) {
          throw new GateoValidationException(
              "OUTPUT node " + nodeIndex + " must have at least one input");
        }
      }
      case AND, OR, XOR -> {
        if (inputCount < 2) {
          throw new GateoValidationException(
              type + " node " + nodeIndex + " must have at least two inputs");
        }
      }
      case NOT -> {
        if (inputCount != 1) {
          throw new GateoValidationException(
              "NOT node " + nodeIndex + " must have exactly one input, got " + inputCount);
        }
      }
      case UNSPECIFIED -> throw new AssertionError("handled earlier");
    }
  }
}
