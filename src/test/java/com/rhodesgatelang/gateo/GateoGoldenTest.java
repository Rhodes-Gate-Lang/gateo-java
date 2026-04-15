package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rhodesgatelang.gateo.v2.GateObject;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class GateoGoldenTest {

  /**
   * Loads {@code minimal.gateo} from test resources (bytes emitted from this library). Replace with
   * a file produced by gatec/gateo-cpp when available for stronger cross-tool coverage.
   */
  @Test
  void goldenMinimalParses() throws Exception {
    GateObject expected = Fixtures.minimalOutput();
    try (InputStream in = getClass().getResourceAsStream("/minimal.gateo")) {
      assertEquals(expected, Gateo.read(in.readAllBytes()));
    }
  }
}
