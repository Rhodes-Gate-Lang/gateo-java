package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rhodesgatelang.gateo.v2.ComponentInstance;
import com.rhodesgatelang.gateo.v2.GateObject;
import com.rhodesgatelang.gateo.v2.GateType;
import com.rhodesgatelang.gateo.v2.Node;
import com.rhodesgatelang.gateo.v2.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
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
}
