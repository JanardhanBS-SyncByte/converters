# Maven module (`kernel-bio-converter/`)

JDK 21, Spring Boot **4.1.1**, Spring Cloud **2025.1.3**. Single module: fat JAR (service) + `-lib` classifier (embeddable). Versions pinned in this `pom.xml` — **no kernel-bom**.

| Artifact | Role |
|----------|------|
| fat JAR | Runnable service (`spring-boot` repackage, ZIP layout) |
| `-lib` | `constant/`, `dto/`, `exception/`, `service/**` only |

**Build:** `mvn clean install "-Dgpg.skip=true"` · tests: `mvn test "-Dgpg.skip=true"` · one class: `mvn test -Dtest=ConverterServiceImplTest "-Dgpg.skip=true"`

Prereqs (install locally for SNAPSHOTs): `commons/kernel` → `kernel-core` / `kernel-auth-adapter` **1.4.1-SNAPSHOT**; `bio-utils` → `biometrics-util` **1.4.1-SNAPSHOT**.

Pipeline: `ConvertController` → `IConverterApi` → `ConverterServiceImpl` → `FingerDecoder` / `FaceDecoder` / `IrisDecoder` (`biometrics-util`).

| Source | Target |
|--------|--------|
| `ISO19794_4_2011` finger | `IMAGE/JPEG`, `IMAGE/PNG`, `ISO19794_4_2011/{JPEG,PNG}` |
| `ISO19794_5_2011` face | `IMAGE/JPEG`, `IMAGE/PNG`, `ISO19794_5_2011/{JPEG,PNG}` |
| `ISO19794_6_2011` iris | `IMAGE/PNG`, `ISO19794_6_2011/PNG` (JPEG variant disabled) |

Errors: `ConversionException` → `MOS-CNV-001`…`012`, `MOS-CNV-500`. Swagger: `/v1/converter-service/swagger-ui/index.html`.

## Rules

- Pin MOSIP deps to **1.4.1-SNAPSHOT**. Do not import `kernel-bom`.
- Jackson versions live in `<properties>` (`jackson-annotations.version`, `jackson-core.version`, `jackson-databind.version` = **2.21.4**).
- Use `spring-boot-jackson2` (Jackson 2) — not Boot 4’s Jackson 3 default.
- Local run: `run-local.bat` / `run-local.sh` (`init` · `start` · `smoke` · `stop` · `test` · `all` · `docker`; env: `PORT`, `SPRING_PROFILES_ACTIVE`, `SPRING_CLOUD_CONFIG_*`, `loader_path_env`).
- Surefire keeps `--add-opens*` / `--enable-preview` — do not remove.
- JaCoCo excludes `constant/`, `config/`, `dto/`, `exception/`, `*Application`; gate ≥95% instruction/line.
- Auth: exclude adapter `SecurityConfig` via `@ComponentScan` REGEX filter; local `SecurityConfig` permits all.
- `-lib` consumers depend with `<classifier>lib</classifier>` — never put the fat JAR on a library classpath.
- Do not scan broad `io.mosip.kernel` / `io.mosip.*` — only `io.mosip.kernel.bio.converter` + auth-adapter basepackage.
- Keep JavaDoc on public classes, fields, and methods when adding or changing APIs.

