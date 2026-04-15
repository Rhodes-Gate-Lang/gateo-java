package com.rhodesgatelang.gateo.v2.internal;

import com.rhodesgatelang.gateo.v2.ComponentInstance;
import com.rhodesgatelang.gateo.v2.GateObject;
import com.rhodesgatelang.gateo.v2.Node;
import com.rhodesgatelang.gateo.v2.Version;
import gateo.v2.Gateo;

/** Converts the native model into generated {@code gateo.v2} protobuf messages. */
public final class V2ToProto {

  private V2ToProto() {}

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
      n.literalValue().ifPresent(nb::setLiteralValue);
      root.addNodes(nb);
    }

    return root.build();
  }
}
