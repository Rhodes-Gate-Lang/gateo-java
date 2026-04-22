# gateo-java

Java library for reading and writing **`.gateo`** files: a single raw protobuf message of type **`gateo.v3.GateObject`**, matching [gateo-cpp](https://github.com/Rhodes-Gate-Lang/gateo-cpp) on the wire.

## Requirements

- **Java 17+** at runtime (library bytecode targets 17).

## Coordinates

| Field | Value |
|-------|--------|
| `groupId` | `com.rhodesgatelang` |
| `artifactId` | `gateo-java` |
| `package` | `com.rhodesgatelang.gateo` (facade) and `com.rhodesgatelang.gateo.v3` (native model) |

Maven Central publishing is not set up in this repository yet; releases attach the main JAR, `-sources.jar`, `-javadoc.jar`, and a matching `.pom` to [GitHub Releases](https://github.com/Rhodes-Gate-Lang/gateo-java/releases). You can install a downloaded release into your local Maven repository with:

```bash
mvn install:install-file \
  -Dfile=gateo-java-3.0.0.jar \
  -DpomFile=gateo-java-3.0.0.pom \
  -Dsources=gateo-java-3.0.0-sources.jar \
  -Djavadoc=gateo-java-3.0.0-javadoc.jar
```

## Quickstart

```java
import com.rhodesgatelang.gateo.Gateo;
import com.rhodesgatelang.gateo.v3.GateObject;
import java.nio.file.Path;

Path file = Path.of("example.gateo");
GateObject model = Gateo.read(file);
Gateo.write(file, model);
```

Avoid importing the generated protobuf entry type **`gateo.v3.Gateo`** in the same compilation unit as **`com.rhodesgatelang.gateo.Gateo`** (simple-name collision). Prefer fully qualified `gateo.v3.Gateo.GateObject` only if you need low-level protobuf access.

## Schema pin

The vendored schema is **gateo-schema [v3.0.0](https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v3.0.0)**. See [schema/PINNED_VERSION.md](schema/PINNED_VERSION.md) for the release zip SHA-256 checked in CI.

**Library major** is intended to track **schema major** (`GateObject.version.major`); this build supports **`version.major == 3`** only (`Gateo.SUPPORTED_SCHEMA_MAJOR`).

## Semantics

- **Unknown protobuf fields** are ignored when parsing to generated types, but mapping into the **native** `com.rhodesgatelang.gateo.v3` model does not preserve them on round-trip (same caveat as gateo-cpp).
- **`Gateo.write(Path, …)`** creates parent directories if needed.
- **`validateBasic`** runs on read (after the version gate) and on write: structural checks (non-empty graph, in-range indices, no self-referential operands) plus v3 bus-operation rules for SPLIT, MERGE, LSL, and LSR. It does not enforce full arity for legacy gate kinds or strict topological order, so newer compiler output stays loadable. Failures throw **`GateoValidationException`**.

## Building

```bash
./gradlew build
```

## License

See [LICENSE](LICENSE).
