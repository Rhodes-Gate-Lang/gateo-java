package com.rhodesgatelang.gateo.v2.internal;

import com.rhodesgatelang.gateo.v2.ComponentInstance;
import com.rhodesgatelang.gateo.v2.GateObject;
import com.rhodesgatelang.gateo.v2.GateType;
import com.rhodesgatelang.gateo.v2.Node;
import com.rhodesgatelang.gateo.v2.Version;
import gateo.v2.Gateo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

/** Converts generated {@code gateo.v2} protobuf messages into the native model. */
public final class V2FromProto {

  private V2FromProto() {}

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
      for (int i = 0; i < n.getInputsCount(); i++) {
        inputs.add(n.getInputs(i));
      }
      Optional<String> name = n.hasName() ? Optional.of(n.getName()) : Optional.empty();
      OptionalLong literal =
          n.hasLiteralValue() ? OptionalLong.of(n.getLiteralValue()) : OptionalLong.empty();
      nodes.add(
          new Node(
              GateType.fromProto(n.getType()),
              inputs,
              n.getWidth(),
              n.getParent(),
              name,
              literal));
    }

    return new GateObject(version, components, nodes);
  }
}
