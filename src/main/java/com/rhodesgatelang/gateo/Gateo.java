package com.rhodesgatelang.gateo;

import com.rhodesgatelang.gateo.v2.GateObject;
import com.rhodesgatelang.gateo.v2.GateObjectValidator;
import com.rhodesgatelang.gateo.v2.internal.V2FromProto;
import com.rhodesgatelang.gateo.v2.internal.V2ToProto;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for reading and writing {@code .gateo} files: raw serialized {@code gateo.v2.GateObject}
 * bytes, matching gateo-cpp wire behavior.
 */
public final class Gateo {

  /** Schema major version supported by this library (wire {@code gateo.v2}). */
  public static final int SUPPORTED_SCHEMA_MAJOR = 2;

  private Gateo() {}

  /** Reads a {@code .gateo} file from the given path. */
  public static GateObject read(Path path) throws GateoIOException {
    try (InputStream in = Files.newInputStream(path)) {
      return read(in, false);
    } catch (GateoIOException e) {
      throw e;
    } catch (InvalidProtocolBufferException e) {
      throw new GateoParseException("Invalid .gateo protobuf in " + path, e);
    } catch (IOException e) {
      throw new GateoIOException("Failed reading " + path, e);
    }
  }

  /**
   * Reads a gate object from a stream. When {@code closeStream} is {@code true}, the stream is closed
   * before returning (including on parse errors).
   */
  public static GateObject read(InputStream in, boolean closeStream) throws GateoIOException {
    try {
      gateo.v2.Gateo.GateObject proto = gateo.v2.Gateo.GateObject.parseFrom(in);
      return fromProto(proto);
    } catch (InvalidProtocolBufferException e) {
      throw new GateoParseException("Invalid .gateo protobuf payload", e);
    } catch (IOException e) {
      throw new GateoIOException("Failed reading gate object stream", e);
    } finally {
      if (closeStream) {
        try {
          in.close();
        } catch (IOException e) {
          throw new GateoIOException("Failed closing input stream", e);
        }
      }
    }
  }

  /** Parses a gate object from its on-wire bytes. */
  public static GateObject read(byte[] data) {
    try {
      return fromProto(gateo.v2.Gateo.GateObject.parseFrom(data));
    } catch (InvalidProtocolBufferException e) {
      throw new GateoParseException("Invalid .gateo protobuf payload", e);
    }
  }

  private static GateObject fromProto(gateo.v2.Gateo.GateObject proto) {
    if (!proto.hasVersion()) {
      throw new GateoParseException("GateObject missing version");
    }
    int major = proto.getVersion().getMajor();
    if (major != SUPPORTED_SCHEMA_MAJOR) {
      throw new VersionException(
          "Unsupported schema major " + major + "; expected " + SUPPORTED_SCHEMA_MAJOR);
    }
    GateObject model = V2FromProto.convert(proto);
    GateObjectValidator.validateBasic(model);
    return model;
  }

  /**
   * Writes a gate object to a path. Parent directories are created if they do not exist.
   *
   * @param path destination file
   * @param object graph to serialize
   */
  public static void write(Path path, GateObject object) throws GateoIOException {
    Path parent = path.getParent();
    if (parent != null) {
      try {
        Files.createDirectories(parent);
      } catch (IOException e) {
        throw new GateoIOException("Failed creating parent directories for " + path, e);
      }
    }
    try (OutputStream out = Files.newOutputStream(path)) {
      write(out, object, false);
    } catch (IOException e) {
      throw new GateoIOException("Failed writing " + path, e);
    }
  }

  /**
   * Serializes a gate object to a stream. When {@code closeStream} is {@code true}, the stream is
   * closed after writing.
   */
  public static void write(OutputStream out, GateObject object, boolean closeStream)
      throws GateoIOException {
    GateObjectValidator.validateBasic(object);
    gateo.v2.Gateo.GateObject proto = V2ToProto.convert(object);
    try {
      proto.writeTo(out);
    } catch (IOException e) {
      throw new GateoIOException("Failed writing gate object", e);
    } finally {
      if (closeStream) {
        try {
          out.close();
        } catch (IOException e) {
          throw new GateoIOException("Failed closing output stream", e);
        }
      }
    }
  }

  /** Returns the on-wire protobuf bytes for {@code object}. */
  public static byte[] toBytes(GateObject object) {
    GateObjectValidator.validateBasic(object);
    return V2ToProto.convert(object).toByteArray();
  }
}
