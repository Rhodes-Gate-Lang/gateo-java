package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rhodesgatelang.gateo.v2.ComponentInstance;
import com.rhodesgatelang.gateo.v2.GateObject;
import com.rhodesgatelang.gateo.v2.GateObjectValidator;
import com.rhodesgatelang.gateo.v2.GateType;
import com.rhodesgatelang.gateo.v2.Node;
import com.rhodesgatelang.gateo.v2.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

class ValidationTest {

  @Test
  void validateBasicRejectsBadTopology() {
    GateObject bad =
        new GateObject(
            new Version(2, 0),
            List.of(new ComponentInstance("Mod", 0)),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty()),
                new Node(
                    GateType.OUTPUT,
                    List.of(),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty())));
    assertThrows(GateoValidationException.class, () -> GateObjectValidator.validateBasic(bad));
  }

  @Test
  void writeRejectsInvalidObject() throws Exception {
    GateObject bad =
        new GateObject(
            new Version(2, 0),
            List.of(new ComponentInstance("Mod", 0)),
            List.of(
                new Node(
                    GateType.INPUT,
                    List.of(0),
                    1,
                    0,
                    Optional.empty(),
                    OptionalLong.empty())));
    Path file = Files.createTempFile("bad", ".gateo");
    assertThrows(GateoValidationException.class, () -> Gateo.write(file, bad));
    Files.deleteIfExists(file);
  }
}
