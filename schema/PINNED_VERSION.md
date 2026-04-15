# Pinned gateo-schema (wire v2)

This library targets **`gateo.v2`** on the wire, as defined by the vendored [`gateo.proto`](../src/main/proto/gateo.proto) copied from the official release asset below.

| Field | Value |
|-------|--------|
| Release tag | `v2.0.1` |
| Release page | https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v2.0.1 |
| Download URL | https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/download/v2.0.1/gateo-schema-v2.0.1.zip |
| SHA-256 (`gateo-schema-v2.0.1.zip`) | `d96f18d7abfaa49542667ed9175fc2e295b72ae08278c66ad05a5d57a039344f` |
| GitHub release `target_commitish` | `main` (see release metadata) |

**Compatibility:** The **gateo-java** artifact version tracks this repository’s releases. **Wire** compatibility is determined by `GateObject.version.major` in serialized objects and by this pinned `.proto`. Intentional pin bumps require updating this file, the vendored proto, and CI checksum expectations together.
