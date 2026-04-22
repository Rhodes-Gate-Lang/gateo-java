package com.rhodesgatelang.gateo.v3.internal;

import com.rhodesgatelang.gateo.v3.ComponentInstance;
import com.rhodesgatelang.gateo.v3.GateObject;
import com.rhodesgatelang.gateo.v3.Node;
import com.rhodesgatelang.gateo.v3.Version;
import gateo.v3.Gateo;

/** Converts the native model into generated {@code gateo.v3} protobuf messages. */
public final class V3ToProto {

  private V3ToProto() {}

  public static Gateo.GateObject convert(GateObject object) {
    Gateo.GateObject.Builder root = Gateo.GateObject.newBuilder();
    Version v = object.version();
    root.setVersion(Gateo.Version.newBuilder().setMajor(v.major()).setMinor(v.minor()));

    for (ComponentInstance c : object.components()) {
      root.addComponents(
          Gateo.ComponentInstance.newBuilder().setName(c.name()).setParent(c.parent()));
    }

    for (Node n : object.nodes()) {
      Gateo.Node.Builder nb =
          Gateo.Node.newBuilder()
              .setType(n.type().toProto())
              .addAllInputs(n.inputs())
              .setWidth(n.width())
              .setParent(n.parent());
      n.name().ifPresent(nb::setName);
      n.value().ifPresent(nb::setValue);
      n.splitLo().ifPresent(nb::setSplitLo);
      root.addNodes(nb);
    }

    return root.build();
  }
}
