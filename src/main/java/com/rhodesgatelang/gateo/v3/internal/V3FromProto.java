package com.rhodesgatelang.gateo.v3.internal;

import com.rhodesgatelang.gateo.v3.ComponentInstance;
import com.rhodesgatelang.gateo.v3.GateObject;
import com.rhodesgatelang.gateo.v3.GateType;
import com.rhodesgatelang.gateo.v3.Node;
import com.rhodesgatelang.gateo.v3.Version;
import gateo.v3.Gateo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

/** Converts generated {@code gateo.v3} protobuf messages into the native model. */
public final class V3FromProto {

  private V3FromProto() {}

  public static GateObject convert(Gateo.GateObject proto) {
    Version version =
        new Version(proto.getVersion().getMajor(), proto.getVersion().getMinor());

    List<ComponentInstance> components = new ArrayList<>(proto.getComponentsCount());
    for (Gateo.ComponentInstance c : proto.getComponentsList()) {
      components.add(new ComponentInstance(c.getName(), c.getParent()));
    }

    List<Node> nodes = new ArrayList<>(proto.getNodesCount());
    for (Gateo.Node n : proto.getNodesList()) {
      List<Integer> inputs = new ArrayList<>(n.getInputsCount());
      for (int j = 0; j < n.getInputsCount(); j++) {
        inputs.add(n.getInputs(j));
      }
      Optional<String> name = n.hasName() ? Optional.of(n.getName()) : Optional.empty();
      OptionalLong value = n.hasValue() ? OptionalLong.of(n.getValue()) : OptionalLong.empty();
      OptionalInt splitLo = n.hasSplitLo() ? OptionalInt.of(n.getSplitLo()) : OptionalInt.empty();
      nodes.add(
          new Node(
              GateType.fromProto(n.getType()),
              inputs,
              n.getWidth(),
              n.getParent(),
              name,
              value,
              splitLo));
    }

    return new GateObject(version, components, nodes);
  }
}
