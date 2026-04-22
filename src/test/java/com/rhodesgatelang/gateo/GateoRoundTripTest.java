package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rhodesgatelang.gateo.v3.GateObject;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class GateoRoundTripTest {

  @Test
  void roundTripBytes() {
    GateObject original = Fixtures.minimalAndOr();
    byte[] bytes = Gateo.toBytes(original);
    GateObject roundTripped = Gateo.read(bytes);
    assertEquals(original, roundTripped);
  }

  @Test
  void roundTripFile() throws Exception {
    GateObject original = Fixtures.minimalAndOr();
    Path file = Files.createTempFile("gateo", ".gateo");
    try {
      Gateo.write(file, original);
      assertEquals(original, Gateo.read(file));
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  void roundTripSplit() {
    GateObject g = Fixtures.splitBus();
    assertEquals(g, Gateo.read(Gateo.toBytes(g)));
  }

  @Test
  void roundTripMerge() {
    GateObject g = Fixtures.mergeBus();
    assertEquals(g, Gateo.read(Gateo.toBytes(g)));
  }

  @Test
  void roundTripLsl() {
    GateObject g = Fixtures.lslBus();
    assertEquals(g, Gateo.read(Gateo.toBytes(g)));
  }

  @Test
  void roundTripLsr() {
    GateObject g = Fixtures.lsrBus();
    assertEquals(g, Gateo.read(Gateo.toBytes(g)));
  }
}
