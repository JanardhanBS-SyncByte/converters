# MOSIP Converters

[![Maven Package upon a push](https://github.com/mosip/converters/actions/workflows/push-trigger.yml/badge.svg?branch=develop)](https://github.com/mosip/converters/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mosip_kernel-bio-converter&metric=alert_status&branch=develop)](https://sonarcloud.io/dashboard?id=mosip_kernel-bio-converter&branch=develop)

ISO/IEC 19794 biometric payloads (finger, face, iris) → JPEG/PNG (and optional ISO re-wrap).
Ships as a **runnable service** and an embeddable **`-lib`** JAR.

| | |
|---|---|
| **JDK** | 21 |
| **Spring Boot** | 4.1.1 (no `kernel-bom`) |
| **Module version** | `1.4.1-SNAPSHOT` |
| **Main class** | `io.mosip.kernel.bio.converter.KernelBioConverterApplication` |
| **HTTP port / context** | `8079` / `/v1/converter-service` |
| **License** | [MPL 2.0](LICENSE) — see [NOTICE](NOTICE) |

**Quick links (after `start`):**

| Purpose | URL |
|---------|-----|
| Health | http://localhost:8079/v1/converter-service/actuator/health |
| Swagger UI | http://localhost:8079/v1/converter-service/swagger-ui/index.html |
| OpenAPI | http://localhost:8079/v1/converter-service/v3/api-docs |
| Convert API | `POST` http://localhost:8079/v1/converter-service/convert |

---

## Artifacts

| Artifact | Classifier | Role |
|----------|------------|------|
| `kernel-bio-converter-${version}.jar` | (none / fat) | Runnable Spring Boot service |
| `kernel-bio-converter-${version}-lib.jar` | `lib` | Embeddable API: `constant/`, `dto/`, `exception/`, `service/**` |
| `…-sources.jar` / `…-javadoc.jar` | — | Sources & JavaDoc for Central |

---

## Prerequisites

- **JDK 21**, **Maven 3.9+**
- **Docker** (optional — for `run-local … docker`)
- Local SNAPSHOTs (or your snapshot repo):
  - `commons/kernel` → `kernel-core`, `kernel-auth-adapter` **1.4.1-SNAPSHOT**
  - `bio-utils` → `biometrics-util` **1.4.1-SNAPSHOT**
- Config: [mosip-config](https://github.com/mosip/mosip-config) (optional for local demos)

---

## Build

```bash
cd kernel-bio-converter
mvn clean install "-Dgpg.skip=true"
```

| Goal | Command |
|------|---------|
| Skip tests | `mvn clean install "-DskipTests=true" "-Dgpg.skip=true"` |
| One test class | `mvn test "-Dtest=ConverterServiceImplTest" "-Dgpg.skip=true"` |
| Coverage | `target/site/jacoco/index.html` (JaCoCo gate **≥ 95%**) |

---

# Local setup

Same helper style as [`commons/kernel/kernel-auth-service`](https://github.com/mosip/commons/tree/master/kernel/kernel-auth-service):
`kernel-bio-converter/run-local.bat` (Windows cmd) and `run-local.sh` (Linux / macOS / WSL / Git Bash).

Working directory must be **`kernel-bio-converter/`**. Logs and PID: `.local/`.

## Helper commands

| Command | What it does |
|---------|----------------|
| `init` | Maven package this module (skip tests) |
| `start` | Start on port **8079**, wait until Spring Boot is ready, print Swagger/health URLs |
| `smoke` | `GET …/actuator/health` and Swagger UI |
| `stop` | Stop the process (frees the Boot ZIP for `mvn clean`) |
| `test` | Maven unit tests |
| `all` | `init` + `test` + `start` + `smoke` |
| `docker` | `init`, docker build, run |
| _(no args)_ / `help` | Usage + endpoint URLs |

`start` uses Generational ZGC and MOSIP `--add-opens` / `--enable-preview` (see scripts). Do **not** put those long JVM flags back into the `Dockerfile`.

## Environment variables

| Variable | Default | Meaning |
|----------|---------|---------|
| `PORT` | `8079` | HTTP port |
| `SPRING_PROFILES_ACTIVE` | `default` | Spring profile |
| `SPRING_CLOUD_CONFIG_URI` | _(empty)_ | Config-server URI |
| `SPRING_CLOUD_CONFIG_LABEL` | _(empty)_ | Config-server label / branch |
| `loader_path_env` | `.` | Extra JARs (`-Dloader.path`) |
| `IMAGE` | `kernel-bio-converter` | Docker image name |
| `JDK_JAVA_OPTIONS` | _(empty)_ | Extra JVM options for `docker` runs |

---

## Windows (cmd)

```bat
cd kernel-bio-converter
run-local.bat init
run-local.bat start
run-local.bat smoke
```

Override port / config server:

```bat
set PORT=8079
set SPRING_PROFILES_ACTIVE=default
set SPRING_CLOUD_CONFIG_URI=http://localhost:51000
set SPRING_CLOUD_CONFIG_LABEL=master
set loader_path_env=.
run-local.bat start
```

Stop / one-shot / Docker:

```bat
run-local.bat stop
run-local.bat all
set SPRING_CLOUD_CONFIG_URI=http://host.docker.internal:51000
run-local.bat docker
```

### Windows PowerShell

```powershell
cd kernel-bio-converter
.\run-local.bat init
.\run-local.bat start
.\run-local.bat smoke
```

```powershell
$env:PORT = "8079"
$env:SPRING_PROFILES_ACTIVE = "default"
$env:SPRING_CLOUD_CONFIG_URI = "http://localhost:51000"
$env:SPRING_CLOUD_CONFIG_LABEL = "master"
.\run-local.bat start
```

---

## Linux / macOS / WSL

```bash
cd kernel-bio-converter
chmod +x run-local.sh
./run-local.sh init
./run-local.sh start
./run-local.sh smoke
```

Override port / config server:

```bash
export PORT=8079
export SPRING_PROFILES_ACTIVE=default
export SPRING_CLOUD_CONFIG_URI=http://localhost:51000
export SPRING_CLOUD_CONFIG_LABEL=master
export loader_path_env=.
./run-local.sh start
```

Or one line:

```bash
PORT=8079 SPRING_CLOUD_CONFIG_URI=http://localhost:51000 ./run-local.sh start
```

Stop / one-shot / Docker:

```bash
./run-local.sh stop
./run-local.sh all
SPRING_CLOUD_CONFIG_URI=http://host.docker.internal:51000 ./run-local.sh docker
```

---

## Windows Git Bash / MSYS

Use the shell script (not `.bat`):

```bash
cd kernel-bio-converter
chmod +x run-local.sh
./run-local.sh init
./run-local.sh start
./run-local.sh smoke
./run-local.sh stop
```

---

## Manual `java -jar` (any OS)

Package first (`run-local … init` or `mvn package "-Dgpg.skip=true"`), then:

**Linux / macOS / WSL / Git Bash:**

```bash
java \
  -XX:-UseG1GC -XX:-UseParallelGC -XX:-UseShenandoahGC \
  -XX:+ExplicitGCInvokesConcurrent -XX:+UseZGC -XX:+ZGenerational \
  -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication \
  -XX:+HeapDumpOnOutOfMemoryError -XX:+UseCompressedOops \
  -XX:MaxGCPauseMillis=200 \
  -Dfile.encoding=UTF-8 \
  -Dloader.path=. \
  -Dspring.profiles.active=default \
  -Dserver.port=8079 \
  -Dspring.cloud.config.uri=http://localhost:51000 \
  -Dspring.cloud.config.label=master \
  --add-modules=ALL-SYSTEM \
  --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/java.lang.stream=ALL-UNNAMED \
  --add-opens java.base/java.time=ALL-UNNAMED \
  --add-opens java.base/java.time.LocalDate=ALL-UNNAMED \
  --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED \
  --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED \
  --add-opens java.base/jdk.internal.reflect.DirectMethodHandleAccessor=ALL-UNNAMED \
  --enable-preview \
  -jar target/kernel-bio-converter-1.4.1-SNAPSHOT.jar
```

**Windows cmd:**

```bat
java ^
  -XX:-UseG1GC -XX:-UseParallelGC -XX:-UseShenandoahGC ^
  -XX:+ExplicitGCInvokesConcurrent -XX:+UseZGC -XX:+ZGenerational ^
  -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication ^
  -XX:+HeapDumpOnOutOfMemoryError -XX:+UseCompressedOops ^
  -XX:MaxGCPauseMillis=200 ^
  -Dfile.encoding=UTF-8 ^
  -Dloader.path=. ^
  -Dspring.profiles.active=default ^
  -Dserver.port=8079 ^
  -Dspring.cloud.config.uri=http://localhost:51000 ^
  -Dspring.cloud.config.label=master ^
  --add-modules=ALL-SYSTEM ^
  --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED ^
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED ^
  --add-opens java.base/java.lang.stream=ALL-UNNAMED ^
  --add-opens java.base/java.time=ALL-UNNAMED ^
  --add-opens java.base/java.time.LocalDate=ALL-UNNAMED ^
  --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED ^
  --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED ^
  --add-opens java.base/jdk.internal.reflect.DirectMethodHandleAccessor=ALL-UNNAMED ^
  --enable-preview ^
  -jar target\kernel-bio-converter-1.4.1-SNAPSHOT.jar
```

Prefer `run-local` — it picks the fat JAR (skips `-lib` / sources / javadoc) and waits for readiness.

---

## IDE samples

Open the **`converters`** (or `kernel-bio-converter`) project. Working directory: **`kernel-bio-converter`**.
Main class: **`io.mosip.kernel.bio.converter.KernelBioConverterApplication`**.

Recommended: run the helper from the IDE terminal / External Tools:

| OS | IDE terminal / External tool |
|----|------------------------------|
| Windows | `run-local.bat init` then `run-local.bat start` |
| Linux / macOS | `./run-local.sh init` then `./run-local.sh start` |

### IntelliJ IDEA

1. **File → Open** → `converters` (or `kernel-bio-converter`).
2. Set Project SDK to **JDK 21**.
3. **Option A — scripts:** Terminal tab → `cd kernel-bio-converter` → `run-local.bat start` / `./run-local.sh start`.
4. **Option B — Application:** Run → Edit Configurations → **Application**
   - Main class: `io.mosip.kernel.bio.converter.KernelBioConverterApplication`
   - Working directory: `$MODULE_DIR$` or absolute `…/kernel-bio-converter`
   - VM options: copy the `-XX:…` / `-D…` / `--add-opens…` / `--enable-preview` block from [Manual `java -jar`](#manual-java--jar-any-os) (without `-jar …`)
   - Environment (optional): `SPRING_PROFILES_ACTIVE=default;PORT=8079;SPRING_CLOUD_CONFIG_URI=http://localhost:51000`

### Eclipse

1. Import as **Existing Maven Project** → `kernel-bio-converter`.
2. Right-click `KernelBioConverterApplication` → **Run As → Java Application**.
3. **Run Configurations → Arguments → VM arguments:** paste the same VM flags as manual `java` (no `-jar`).
4. **Environment** tab: add `SPRING_PROFILES_ACTIVE`, `SPRING_CLOUD_CONFIG_URI` as needed.
5. Or use **External Tools** → Program: `run-local.bat` / `run-local.sh`, Working Directory: module folder, Arguments: `start`.

### VS Code / Cursor

`.vscode/launch.json` example:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "KernelBioConverterApplication",
      "request": "launch",
      "mainClass": "io.mosip.kernel.bio.converter.KernelBioConverterApplication",
      "projectName": "kernel-bio-converter",
      "cwd": "${workspaceFolder}/kernel-bio-converter",
      "vmArgs": "-XX:-UseG1GC -XX:-UseParallelGC -XX:-UseShenandoahGC -XX:+ExplicitGCInvokesConcurrent -XX:+UseZGC -XX:+ZGenerational -XX:+UnlockExperimentalVMOptions -XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:+UseCompressedOops -XX:MaxGCPauseMillis=200 -Dfile.encoding=UTF-8 -Dloader.path=. -Dspring.profiles.active=default -Dserver.port=8079 --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect.DirectMethodHandleAccessor=ALL-UNNAMED --enable-preview",
      "env": {
        "SPRING_PROFILES_ACTIVE": "default"
      }
    }
  ]
}
```

Or integrated terminal:

```bash
cd kernel-bio-converter && ./run-local.sh start   # Linux / macOS / Git Bash
cd kernel-bio-converter && run-local.bat start    # Windows
```

### NetBeans

1. Open `kernel-bio-converter` as a Maven project.
2. Open `KernelBioConverterApplication` → **Run File**.
3. **Project Properties → Run → VM Options:** paste the same flags as manual `java`.
4. Or **Tools → External Tools** pointing at `run-local.bat` / `run-local.sh` with argument `start`.

---

## Docker

**Via helper (preferred):**

```bat
REM Windows
set SPRING_CLOUD_CONFIG_URI=http://host.docker.internal:51000
run-local.bat docker
```

```bash
# Linux / macOS / Git Bash
SPRING_CLOUD_CONFIG_URI=http://host.docker.internal:51000 ./run-local.sh docker
```

**Manual:**

```bash
cd kernel-bio-converter
mvn clean package "-Dgpg.skip=true" "-DskipTests"
docker build -t kernel-bio-converter:latest -f Dockerfile .
```

macOS / Windows Docker Desktop:

```bash
docker run --rm -p 8079:8079 --name kernel-bio-converter \
  -e active_profile_env=default \
  -e spring_config_url_env=http://host.docker.internal:51000 \
  -e spring_config_label_env=master \
  kernel-bio-converter:latest
```

Linux (reach config server on host):

```bash
docker run --rm -p 8079:8079 --name kernel-bio-converter \
  --add-host=host.docker.internal:host-gateway \
  -e active_profile_env=default \
  -e spring_config_url_env=http://host.docker.internal:51000 \
  -e spring_config_label_env=master \
  kernel-bio-converter:latest
```

Local ZGC / `--add-opens` stay in **`run-local.bat` / `run-local.sh`**. The image uses a short `java -jar` entrypoint plus config env vars.

Helm / cluster: [`deploy/`](deploy/) and [`helm/converters/`](helm/converters/).

---

## Use as a library

```xml
<dependency>
  <groupId>io.mosip.kernel</groupId>
  <artifactId>kernel-bio-converter</artifactId>
  <version>${kernel.bioconverter.version}</version>
  <classifier>lib</classifier>
</dependency>
```

```java
Map<String, String> out = converterApi.convert(
    values, "ISO19794_4_2011", "IMAGE/JPEG",
    Map.of(), Map.of());
```

---

## API

**`POST /v1/converter-service/convert`**

```json
{
  "id": "mosip.converter",
  "version": "1.0",
  "requesttime": "2024-01-01T00:00:00.000Z",
  "request": {
    "values": { "Left IndexFinger": "<base64url ISO blob>" },
    "sourceFormat": "ISO19794_4_2011",
    "targetFormat": "IMAGE/JPEG",
    "sourceParameters": {},
    "targetParameters": {}
  }
}
```

### Formats

| Source | Targets |
|--------|---------|
| `ISO19794_4_2011` (finger JP2000/WSQ) | `IMAGE/JPEG`, `IMAGE/PNG`, `ISO19794_4_2011/{JPEG,PNG}` |
| `ISO19794_5_2011` (face JP2000) | `IMAGE/JPEG`, `IMAGE/PNG`, `ISO19794_5_2011/{JPEG,PNG}` |
| `ISO19794_6_2011` (iris MONO_JPEG2000) | `IMAGE/PNG`, `ISO19794_6_2011/PNG` (JPEG variant disabled) |

### Error codes

| Code | Meaning |
|------|---------|
| MOS-CNV-001 … MOS-CNV-012 | Validation / format / decode failures |
| MOS-CNV-500 | Unexpected technical error |

---

## Project layout

| Path | Role |
|------|------|
| `kernel-bio-converter/` | Maven module, `run-local.bat` / `run-local.sh`, Dockerfile |
| `helm/converters/` | Helm chart |
| `deploy/` | Cluster install helpers |
| `AGENTS.md` | AI / contributor quick rules |

---

## Contributing

- Code contributions: [MOSIP docs](https://docs.mosip.io/1.2.0/community/code-contributions)
- Community: [community.mosip.io](https://community.mosip.io/)
- Issues: [github.com/mosip/converters/issues](https://github.com/mosip/converters/issues)

## License

[Mozilla Public License 2.0](LICENSE). Third-party notices: [NOTICE](NOTICE).
