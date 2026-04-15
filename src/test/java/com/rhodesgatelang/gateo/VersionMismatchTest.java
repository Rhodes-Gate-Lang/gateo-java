package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class VersionMismatchTest {

  @Test
  void rejectsUnsupportedMajor() {
    gateo.v2.Gateo.GateObject proto =
        gateo.v2.Gateo.GateObject.newBuilder()
            .setVersion(gateo.v2.Gateo.Version.newBuilder().setMajor(1).setMinor(0))
            .addComponents(gateo.v2.Gateo.ComponentInstance.newBuilder().setName("X").setParent(0))
            .addNodes(
                gateo.v2.Gateo.Node.newBuilder()
                    .setType(gateo.v2.Gateo.GateType.GATE_TYPE_INPUT)
                    .setWidth(1)
                    .setParent(0)
                    .setName("a"))
            .addNodes(
                gateo.v2.Gateo.Node.newBuilder()
                    .setType(gateo.v2.Gateo.GateType.GATE_TYPE_OUTPUT)
                    .addInputs(0)
                    .setWidth(1)
                    .setParent(0))
            .build();

    byte[] bytes = proto.toByteArray();
    assertThrows(VersionException.class, () -> Gateo.read(bytes));
  }
}
