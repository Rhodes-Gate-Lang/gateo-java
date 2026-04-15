package com.rhodesgatelang.gateo.v2;

import com.rhodesgatelang.gateo.GateoValidationException;
import java.util.List;

/**
 * Lightweight structural checks for native {@link GateObject} graphs: non-empty graph, in-range
 * indices, and no self-referential node operands. Deliberately does <em>not</em> enforce gate arity
 * or topological order so early compiler output can still be loaded.
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
    }
  }
}
