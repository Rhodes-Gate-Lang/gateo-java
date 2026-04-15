# gateo-java

Java library for reading and writing **`.gateo`** files: a single raw protobuf message of type **`gateo.v2.GateObject`**, matching [gateo-cpp](https://github.com/Rhodes-Gate-Lang/gateo-cpp) on the wire.

## Requirements

- **Java 17+** at runtime (library bytecode targets 17).

## Coordinates

| Field | Value |
|-------|--------|
| `groupId` | `com.rhodesgatelang` |
| `artifactId` | `gateo-java` |
| `package` | `com.rhodesgatelang.gateo` (facade) and `com.rhodesgatelang.gateo.v2` (native model) |

Maven Central publishing is not set up in this repository yet; releases attach the main JAR, `-sources.jar`, `-javadoc.jar`, and a matching `.pom` to [GitHub Releases](https://github.com/Rhodes-Gate-Lang/gateo-java/releases). You can install a downloaded release into your local Maven repository with:

```bash
mvn install:install-file \
  -Dfile=gateo-java-2.0.2.jar \
  -DpomFile=gateo-java-2.0.2.pom \
  -Dsources=gateo-java-2.0.2-sources.jar \
  -Djavadoc=gateo-java-2.0.2-javadoc.jar
```

## Quickstart

```java
import com.rhodesgatelang.gateo.Gateo;
import com.rhodesgatelang.gateo.v2.GateObject;
import java.nio.file.Path;

Path file = Path.of("example.gateo");
GateObject model = Gateo.read(file);
Gateo.write(file, model);
```

Avoid importing the generated protobuf entry type **`gateo.v2.Gateo`** in the same compilation unit as **`com.rhodesgatelang.gateo.Gateo`** (simple-name collision). Prefer fully qualified `gateo.v2.Gateo.GateObject` only if you need low-level protobuf access.

## Schema pin

The vendored schema is **gateo-schema [v2.0.1](https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v2.0.1)**. See [schema/PINNED_VERSION.md](schema/PINNED_VERSION.md) for the release zip SHA-256 checked in CI.

**Library major** is intended to track **schema major** (`GateObject.version.major`); this build supports **`version.major == 2`** only (`Gateo.SUPPORTED_SCHEMA_MAJOR`).

## Semantics

- **Unknown protobuf fields** are ignored when parsing to generated types, but mapping into the **native** `com.rhodesgatelang.gateo.v2` model does not preserve them on round-trip (same caveat as gateo-cpp).
- **`Gateo.write(Path, …)`** creates parent directories if needed.
- **`validateBasic`** runs on read (after the version gate) and on write: only light structural checks (non-empty graph, in-range indices, no self-referential operands). It does not enforce per-gate input counts or strict topological order, so newer compiler output stays loadable. Failures throw **`GateoValidationException`**.

## Building

```bash
./gradlew build
```

## License

See [LICENSE](LICENSE).
