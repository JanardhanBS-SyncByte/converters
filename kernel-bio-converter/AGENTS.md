# kernel-bio-converter/

```
src/main/java/.../bio/converter/
├── KernelBioConverterApplication   # exclude DS/JPA + adapter SecurityConfig
├── controller/ConvertController    # POST /convert
├── service/IConverterApi → impl/ConverterServiceImpl
├── dto/ · constant/ · exception/ · config/
└── resources/ bootstrap + application (no DB)

artifacts/
├── *.jar           # fat Boot ZIP
└── *-lib.jar       # constant|dto|exception|service/**

run-local.bat|sh → init|start|smoke|stop|test|all|docker   # :8079 /v1/converter-service
```

**Pins:** Boot**4.1.1** · Cloud**2025.1.3** · springdoc**3.1.1** · `jackson2.bom`**2.22.3** · MOSIP**1.4.1-SNAPSHOT** (`kernel-core`·`auth-adapter`·`biometrics-util`)

**Flow:** Controller → ServiceImpl → Finger/Face/IrisDecoder (`biometrics-util`) · errors `MOS-CNV-001…012|500`

**Rules:** no `kernel-bom` · `spring-boot-jackson2` · scan only `bio.converter`+adapter pkg · local `SecurityConfig` permitAll · `-lib`=`<classifier>lib` · Surefire `--add-opens*`/`--enable-preview` · JaCoCo≥95% (excl constant/config/dto/exception/*Application) · JavaDoc on public API

**Build:** `mvn clean install "-Dgpg.skip=true"`
