# gateo-java — implementation plan

This document is a blueprint for building **gateo-java** as the Java counterpart to **gateo-cpp**: protobuf on the wire, a **clean native Java model**, **automated `.gateo` I/O**, a **pinned schema** from the official **gateo-schema** release, and **GitHub Actions** that produce **consumable releases** for external tools.

**Alignment note (schema major):** [gateo-cpp](https://github.com/Rhodes-Gate-Lang/gateo-cpp) at `v2.0.1` documents **`gateo.v2`** on the wire and pins the zip from [gateo-schema v2.0.1](https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v2.0.1). The sibling repo **gateo-schema** still contains a historical `gateo/v1/gateo.proto`, but current interop with `.gateo` producers/consumers in this ecosystem targets **v2**. This plan assumes **v2** unless you explicitly need a legacy **v1** line (which would be a separate artifact and package namespace).

---

## 1. Requirements traceability

| # | Requirement | Plan summary |
|---|-------------|--------------|
| 1 | Clean Java object, normal conventions | Immutable **records** (Java 17+) or **immutable POJOs** with `List`/`Optional` for optional fields; **separate package** from generated protobuf; **null-hostile** API (no null lists); **Javadoc** on public surface. |
| 2 | Automated I/O for `.gateo` | Single entry type (e.g. `Gateo`) with `read(Path)`, `read(InputStream)`, `read(byte[])`, and symmetric `write*`; binary format = **`GateObject` protobuf message** only (same as gateo-cpp). |
| 3 | Pinned `.proto` from release **v2.0.1** | **Vendor** the exact `gateo.proto` from asset `gateo-schema-v2.0.1.zip` under `src/main/proto/` (or `schema/pinned/v2/gateo.proto` + copy into proto source set). Record **tag, zip SHA-256, and git commit** from the release in `schema/PINNED_VERSION.md` (or a small `schema-version.properties`). CI step: **verify checksum** against GitHub release asset so drift is impossible. |
| 4 | GitHub CI + releases for external tools | **`ci.yml`**: PR/push, JDK matrix (e.g. 17 and 21), build, unit tests, checksum verification of pinned schema. **`release.yml`**: on semver tag `v*`, build, sign (optional), attach **`gateo-java-<version>.jar`**, **`-sources.jar`**, **`-javadoc.jar`**, and a **`pom.xml`** (or Gradle module metadata) so tools can depend without cloning. |
| 5 | High quality, extensible to future schema majors | Mirror **gateo-cpp** layering: **`...gateo.v2`** native model + **`...gateo.v2.internal`** (or package-private) converters; **version gate** on `Version.major`; reserve **`com...gateo.v3`** (future) without breaking v2; keep converters **thin** and test with **golden `.gateo`** blobs. |

---

## 2. Target architecture (mirror gateo-cpp)

**Layers:**

1. **Generated code** — `protoc` Java output for `package gateo.v2` (protobuf’s own package layout). Keep this **implementation detail**: do not expose generated types as the primary public API unless you add a clearly named “advanced” package for power users.
2. **Native model** — e.g. `com.rhodesgatelang.gateo.v2.GateObject`, `Node`, `ComponentInstance`, `Version`, `GateType` (Java `enum`). Map protobuf `optional` to `Optional<>`; `repeated` to **immutable** `List` copies on read.
3. **Conversion** — package-private or `internal` converters: `GateObjectProto ↔ GateObject` (same semantics as `gateo-cpp` `from_proto` / `to_proto`; document that **unknown fields are not preserved** round-tripping through the native model).
4. **I/O + validation** — After parse, require `version.major == SUPPORTED_SCHEMA_MAJOR` (constant **2**, matching gateo-cpp’s `supported_schema_major`). Run a minimal **`validateBasic`** (non-empty components for root, etc.) like C++ `validate_basic`.

**Public API sketch (names adjustable):**

- `com.rhodesgatelang.gateo.Gateo` — façade for I/O.
- `com.rhodesgatelang.gateo.VersionException`, `GateoParseException`, `GateoIOException` — typed errors (avoid raw `IOException` only where it adds clarity).

---

## 3. Build system choice

**Recommendation: Gradle (Kotlin DSL)** with the **protobuf Gradle plugin**:

- Generates Java from the vendored `.proto` in a standard layout.
- Publishes **JAR + POM** to GitHub Releases via a small workflow step (`mvn deploy` is alternative if you prefer Maven).

**Minimum Java version:** **17** (records, clearer `Optional`, good LTS). CI matrix **17 + 21** matches common library practice.

**Dependencies:**

- `com.google.protobuf:protobuf-java` — version pinned in `gradle/libs.versions.toml` (or `gradle.properties`); track a current stable 3.x / 4.x line consistently with what `protoc` generates (keep **protoc** and **runtime** versions aligned per protobuf release notes).

---

## 4. Pinning the schema (requirement 3, in detail)

**Source of truth:** The published asset naming used by gateo-cpp CMake:  
`https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/download/v2.0.1/gateo-schema-v2.0.1.zip`  
(see gateo-cpp `cmake/GateoFetchSchema.cmake` in this workspace.)

**Steps:**

1. Download that zip locally once, extract `gateo.proto`, place it under **`gateo-java/src/main/proto/gateo.proto`** (flat name matches the release artifact layout expected by gateo-cpp) **or** preserve subdirectory if the zip contains one — match **whatever the zip actually contains** and set `proto` path accordingly.
2. Add **`schema/PINNED_VERSION.md`** containing:
   - Release URL: `https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v2.0.1`
   - Expected SHA-256 of `gateo-schema-v2.0.1.zip` (fill after first download).
   - Short note: “Java library `artifactVersion` tracks gateo-java; **wire** compatibility is defined by `GateObject.version.major` and this pinned proto.”
3. **CI verification job:** download the same zip, hash it, compare to the recorded SHA-256; fail if mismatch (catches accidental local edits or wrong file).

**Policy:** Do **not** hand-edit the vendored proto except when intentionally bumping the pin; bumps are a **deliberate PR** that updates SHA doc + regenerates anything needed.

---

## 5. `.gateo` I/O (requirement 2, in detail)

**Format:** Length-delimited **single message** is *not* specified in the proto file itself; **gateo-cpp** uses `SerializeToOstream` / `ParseFromIstream` on a **single** `gateo.v2.GateObject` — i.e. **raw protobuf message bytes** as the entire file. Java must match exactly:

- **Read:** `GateObject.parseFrom(byte[])` or `parseFrom(InputStream)` then convert to native model after **major version** check.
- **Write:** convert native → protobuf builder, then `writeTo(OutputStream)` / `toByteArray()`.

**API surface:**

- `Gateo.read(Path path)`
- `Gateo.read(InputStream in, boolean closeStream)` (or try-with-resources documented)
- `Gateo.read(byte[] data)`
- `Gateo.write(Path path, GateObject obj)` — create parent dirs policy: either document “parent must exist” or create parent directories explicitly (pick one and test).

**Encoding:** Binary only; no Base64 inside the library (callers can wrap if needed).

---

## 6. Native Java model (requirement 1, in detail)

**Conventions:**

- **Immutability:** Prefer Java **records** for `Version`, `Node`, `ComponentInstance`, and top-level `GateObject` if constructor arity stays manageable; otherwise immutable classes with factory methods.
- **Collections:** Expose `List<>` as **immutable** views (`List.copyOf`) or persistent copies; never return mutable internal lists.
- **Enums:** `GateType` mirrors protobuf enum names in **idiomatic Java** (`INPUT`, `AND`, …) with explicit conversion to protobuf enum in converters.
- **Optional fields:** Map `optional string name` → `Optional<String>`; `optional uint64 literal_value` → `OptionalLong` or `Optional<Long>` (pick one style and use consistently).
- **Validation:** `validateBasic(GateObject)` throws a dedicated unchecked or checked exception type — match team taste; gateo-cpp uses `ValidateError` as `runtime_error`.

**Equality / hashing:** Records give you `equals`/`hashCode` for free — useful for tests and tooling.

---

## 7. Testing strategy

| Test | Purpose |
|------|---------|
| **Round-trip** | Build a small in-memory `GateObject`, write to bytes, read back, assert equality. |
| **Golden file** | Commit one or more `.gateo` files produced by **gatec** / **gateo-cpp** (from this workspace’s `gatec/build/gateo-cache/*.gateo` if they are v2) and assert parse + stable properties. |
| **Version reject** | Mutate or synthesize protobuf with `version.major != 2`, expect `VersionException`. |
| **Checksum** | CI verifies release zip hash. |

Before relying on cached `.gateo` files, confirm they were emitted with **v2** schema (inspect with a one-off `protoc` / small Java main if needed).

---

## 8. CI and releases (requirement 4, in detail)

### 8.1 `/.github/workflows/ci.yml`

- Triggers: `push` / `pull_request` to `main`.
- Jobs:
  - **verify-schema-pin:** download `gateo-schema-v2.0.1.zip`, verify SHA-256.
  - **build:** matrix `ubuntu-latest` × JDK `[17, 21]` (add `macos-latest` optionally for parity with gateo-cpp).
  - Steps: checkout, setup JDK (Temurin), **cache Gradle**, `./gradlew build` (or `test`), upload test reports on failure.

### 8.2 `/.github/workflows/release.yml`

- Triggers: `push` tags matching `v[0-9]+.[0-9]+.[0-9]+`.
- Permissions: `contents: write`.
- Steps:
  1. Verify schema pin (same as CI).
  2. `./gradlew build` with **release** version derived from tag (`GITHUB_REF_NAME`).
  3. Produce artifacts:
     - Main library JAR (no “fat jar” required unless you want to shade protobuf — **prefer not shading** so consumers dedupe protobuf).
     - `-sources.jar`, `-javadoc.jar` via Gradle Java plugin conventions.
     - **`pom.xml`** suitable for `mvn install:install-file` **or** publish via **`maven-publish`** to GitHub Packages / Sonatype — minimum bar from the requirement text is **GitHub Release attachments**.
  4. `softprops/action-gh-release@v2` (same family as gateo-cpp) with `files:` listing the JARs + POM.

**Versioning policy:** Tag **gateo-java** with the same **semver** as gateo-cpp when they pin the same schema (e.g. **`v2.0.1`**) so tooling can line up “I need gateo-java v2.0.1 with gateo-schema v2.0.1.” Document that **library major** tracks **schema major**, not necessarily every minor protobuf field change.

---

## 9. Future schema versions (requirement 5)

**Package layout:**

- `...gateo.v2` — current native types and converters.
- Future: `...gateo.v3` alongside v2 in the same repo **or** a major branch — decision when v3 exists.

**Reading strategy for a multi-major world:**

- Option A (simple): Public API only supports v2 until v3 ships; `Gateo.read` throws if major ≠ 2.
- Option B (extensible): Internal `GateObjectReader` that peeks `version.major` (parse-delimited or partial parse — protobuf Java may need **parse from byte[]** then inspect) and dispatches to `V2Model` / `V3Model`. Only invest in B when v3 is real.

**Converters:** Keep **one class per direction per major** (`V2FromProto`, `V2ToProto`) to avoid a giant unmaintainable converter.

---

## 10. Suggested repository layout (after implementation)

```
gateo-java/
  IMPLEMENTATION_PLAN.md          # this file
  README.md                       # user-facing: coordinates, quickstart, versioning table
  LICENSE
  settings.gradle.kts
  build.gradle.kts
  gradle/libs.versions.toml
  schema/
    PINNED_VERSION.md             # tag, URL, SHA-256 of release zip
  src/main/proto/
    gateo.proto                   # exact copy from gateo-schema-v2.0.1.zip
  src/main/java/com/rhodesgatelang/gateo/
    Gateo.java                    # I/O façade
    VersionException.java
    ...
    v2/
      GateObject.java             # native model
      GateType.java
      GateObjectValidator.java
      internal/
        V2ProtobufMapper.java     # package-private conversion
  src/test/java/...               # unit tests + golden resources
  src/test/resources/*.gateo      # golden files
  .github/workflows/ci.yml
  .github/workflows/release.yml
```

Adjust `groupId` / base package to your actual Maven coordinates (example uses a placeholder org name consistent with **Rhodes-Gate-Lang**).

---

## 11. Implementation checklist (for a focused agent run)

Use this as a single-session or multi-session task list inside **only** `gateo-java/`.

1. **Bootstrap Gradle project** — application vs library: use **Java library** plugin; enable **Javadoc** + **sources** jars.
2. **Vendor proto** — populate `src/main/proto` from `gateo-schema-v2.0.1.zip`; add `schema/PINNED_VERSION.md` with SHA-256.
3. **Wire protobuf plugin** — generate Java into `build/generated/...`; add to source sets.
4. **Implement native v2 model + mapper + validator** — follow gateo-cpp semantics.
5. **Implement `Gateo` I/O** — version gate + exceptions + tests.
6. **Add golden + round-trip tests** — prove compatibility with gateo-cpp / gatec outputs.
7. **Add GitHub Actions** — CI + release with attached JARs/POM.
8. **README** — “How to depend” (Maven coordinate after Central publish, or `install-file` / direct JAR from Releases), compatibility table, link to [gateo-schema v2.0.1](https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v2.0.1).

---

## 12. Non-goals (initially)

- **JSON / text** alternate encoding for `.gateo`.
- **Automatic forward compatibility** beyond protobuf’s ignoring unknown fields **when parsing to generated types** — the native model path still drops unknowns when mapping; document this like gateo-cpp.
- **Publishing to Maven Central** — optional follow-up; GitHub Releases + POM already satisfies “external tools can include” if documented.

---

## 13. Quality bar (“multiple passes”)

Before calling the work done:

1. **Pass 1 — Correctness:** tests green, golden files load, version mismatch throws.
2. **Pass 2 — API review:** public types minimal; internal packages hidden; Javadoc on public members.
3. **Pass 3 — Build hygiene:** reproducible builds (`--no-build-cache` in CI optional), dependency pinning, no warnings treated as errors unless team wants `-Werror` style for Java.
4. **Pass 4 — Release dry-run:** tag a `v0.0.0-test` on a fork or branch protection test repo to validate workflow uploads once (optional but high confidence).

This plan should give an agent enough context to implement **gateo-java** in isolation while staying wire-compatible with **gateo-cpp** and the **v2.0.1** schema pin.
