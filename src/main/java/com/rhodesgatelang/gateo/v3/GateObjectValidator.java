package com.rhodesgatelang.gateo.v3;

import com.rhodesgatelang.gateo.GateoValidationException;
import java.util.List;

/**
 * Lightweight structural checks for native {@link GateObject} graphs: non-empty graph, in-range
 * indices, no self-referential node operands, and v3 bus-operation constraints. Deliberately does
 * <em>not</em> enforce full gate arity for legacy types or strict topological order so early
 * compiler output can still be loaded.
 */
public final class GateObjectValidator {

  private GateObjectValidator() {}

  /**
   * @throws GateoValidationException when the object fails basic structural checks
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
    int nodeCount = nodes.size();

    for (int i = 0; i < nodeCount; i++) {
      Node node = nodes.get(i);
      if (node.type() == GateType.UNSPECIFIED) {
        throw new GateoValidationException("node " + i + " has UNSPECIFIED gate type");
      }
      int parent = node.parent();
      if (parent < 0 || parent >= components.size()) {
        throw new GateoValidationException("node " + i + " has invalid component parent " + parent);
      }

      List<Integer> inputs = node.inputs();
      for (int inIdx = 0; inIdx < inputs.size(); inIdx++) {
        int operand = inputs.get(inIdx);
        if (operand < 0 || operand >= nodeCount) {
          throw new GateoValidationException(
              "node "
                  + i
                  + " input "
                  + inIdx
                  + " must reference a valid node index in [0, "
                  + nodeCount
                  + "), got "
                  + operand);
        }
        if (operand == i) {
          throw new GateoValidationException(
              "node " + i + " input " + inIdx + " cannot reference itself");
        }
      }

      validateBusOps(object, i, node);
    }
  }

  private static void validateBusOps(GateObject object, int i, Node node) {
    List<Node> nodes = object.nodes();
    switch (node.type()) {
      case SPLIT -> {
        if (node.inputs().size() != 1) {
          throw new GateoValidationException(
              "node " + i + " SPLIT must have exactly one input, got " + node.inputs().size());
        }
        if (node.splitLo().isEmpty()) {
          throw new GateoValidationException("node " + i + " SPLIT requires split_lo");
        }
        int splitLo = node.splitLo().getAsInt();
        if (splitLo < 0) {
          throw new GateoValidationException("node " + i + " SPLIT split_lo must be non-negative");
        }
        int inW = nodes.get(node.inputs().get(0)).width();
        if ((long) splitLo + node.width() > inW) {
          throw new GateoValidationException(
              "node "
                  + i
                  + " SPLIT range [split_lo, split_lo+width) exceeds input width "
                  + inW);
        }
      }
      case MERGE -> {
        if (node.inputs().size() < 2) {
          throw new GateoValidationException(
              "node " + i + " MERGE must have at least two inputs, got " + node.inputs().size());
        }
        int sum = 0;
        for (int idx : node.inputs()) {
          sum += nodes.get(idx).width();
        }
        if (sum != node.width()) {
          throw new GateoValidationException(
              "node "
                  + i
                  + " MERGE output width "
                  + node.width()
                  + " must equal sum of input widths "
                  + sum);
        }
      }
      case LSL, LSR -> {
        if (node.inputs().size() != 2) {
          throw new GateoValidationException(
              "node "
                  + i
                  + " "
                  + node.type()
                  + " must have exactly two inputs, got "
                  + node.inputs().size());
        }
        Node shiftAmount = nodes.get(node.inputs().get(1));
        if (shiftAmount.type() != GateType.LITERAL) {
          throw new GateoValidationException(
              "node " + i + " " + node.type() + " second operand must be a LITERAL node");
        }
      }
      default -> {}
    }
  }
}
