# Pinned gateo-schema (wire v3)

This library targets **`gateo.v3`** on the wire, as defined by the vendored [`gateo.proto`](../src/main/proto/gateo.proto) copied from the official release asset below.

| Field | Value |
|-------|--------|
| Release tag | `v3.0.0` |
| Release page | https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/tag/v3.0.0 |
| Download URL | https://github.com/Rhodes-Gate-Lang/gateo-schema/releases/download/v3.0.0/gateo-schema-v3.0.0.zip |
| SHA-256 (`gateo-schema-v3.0.0.zip`) | `ccf83ed8b4e7fb5990ef399f818232ab2c156ec73d4d5d5ef48072ac3946ca89` |
| GitHub release `target_commitish` | `main` (see release metadata) |

**Compatibility:** The **gateo-java** artifact version tracks this repository’s releases. **Wire** compatibility is determined by `GateObject.version.major` in serialized objects and by this pinned `.proto`. Intentional pin bumps require updating this file, the vendored proto, and CI checksum expectations together.
