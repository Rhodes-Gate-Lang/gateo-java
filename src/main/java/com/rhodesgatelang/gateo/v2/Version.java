package com.rhodesgatelang.gateo.v2;

/** Schema version carried on the wire inside every {@link GateObject}. */
public record Version(int major, int minor) {}
