package com.rhodesgatelang.gateo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ParseFailureTest {

  @Test
  void rejectsGarbageBytes() {
    assertThrows(GateoParseException.class, () -> Gateo.read(new byte[] {0x01, 0x02, 0x03}));
  }
}
