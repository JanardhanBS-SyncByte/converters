# converters

ISO→JPEG/PNG. **JDK21 · Boot4.1.1 · no kernel-bom.** Load only the nested `AGENTS.md` for the folder you edit.

```
converters/
├── kernel-bio-converter/   # Java · run-local.* · Dockerfile
├── helm/converters/        # chart
├── deploy/                 # install.sh
├── licenses/ · NOTICE · LICENSE
└── README.md               # how-to (prefer over expanding this file)
```

**Build/run:** `cd kernel-bio-converter && mvn clean install "-Dgpg.skip=true"` · `run-local.bat|sh` `init|start|smoke|stop|test|all|docker`

**Rules:** one folder · Grep/Glob · skip `target/` · pins `1.4.1-SNAPSHOT` · no `kernel-bom`/`kernel-logger-logback` · Jackson2 via `jackson2.bom.version`(**2.22.3**) never Boot `jackson-bom.version` · freeze `POST /v1/converter-service/convert` + `-lib` · JaCoCo≥95% · GC flags only in `run-local.*` not Dockerfile
