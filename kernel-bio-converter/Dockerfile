FROM mosipid/openjdk-21-jre:21.0.4

# Labels assigned at Docker build time [Mandatory]
ARG SOURCE
ARG COMMIT_HASH
ARG COMMIT_ID
ARG BUILD_TIME
LABEL source=${SOURCE}
LABEL commit_hash=${COMMIT_HASH}
LABEL commit_id=${COMMIT_ID}
LABEL build_time=${BUILD_TIME}

# Can be passed during Docker build as build-time environment for github branch to pick up configuration from.
ARG spring_config_label=master

# Can be passed during Docker build as build-time environment for spring profiles active.
ARG active_profile=default

# Can be passed during Docker build as build-time environment for config server URL.
ARG spring_config_url

# Can be passed during Docker build as build-time environment for container user.
ARG container_user=mosip

# Can be passed during Docker build as build-time environment for container user group.
ARG container_user_group=mosip

# Can be passed during Docker build as build-time environment for container user uid.
ARG container_user_uid=1001

# Can be passed during Docker build as build-time environment for container user gid.
ARG container_user_gid=1001

# Environment variable to pass active profile such as DEV, QA etc at docker runtime.
# Environment variable to pass github branch to pick up configuration from, at docker runtime.
# Environment variable to pass spring configuration url, at docker runtime.
# Working directory for the container user.
# Extra jars path (loader.path) under the user home.
ENV active_profile_env=${active_profile} \
    spring_config_label_env=${spring_config_label} \
    spring_config_url_env=${spring_config_url} \
    work_dir=/home/${container_user} \
    loader_path_env=/home/${container_user}/additional_jars/

# Set working directory for the user
WORKDIR ${work_dir}

# Install packages and create user
RUN apt-get update && \
    apt-get install -y --no-install-recommends sudo && \
    groupadd -g ${container_user_gid} ${container_user_group} && \
    useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/bash -m ${container_user} && \
    mkdir -p ${work_dir}/additional_jars && \
    rm -rf /var/lib/apt/lists/*

# Copy module jars into work dir (keep original names: fat, -lib, -sources, -javadoc may be present).
# Destination must end with / so Docker does not rename a single match to kernel-bio-converter.jar.
COPY ./target/kernel-bio-converter-*.jar ${work_dir}/

# Prefer Boot fat JAR only; drop -lib / -sources / -javadoc / .original.
# Change permissions of files inside working dir.
RUN set -eux; \
    cd ${work_dir}; \
    rm -f kernel-bio-converter-*-lib.jar \
          kernel-bio-converter-*-sources.jar \
          kernel-bio-converter-*-javadoc.jar \
          kernel-bio-converter-*.jar.original \
          kernel-bio-converter.jar.original 2>/dev/null || true; \
    fat=""; \
    for f in kernel-bio-converter-*.jar; do \
      [ -f "$f" ] || continue; \
      case "$f" in \
        *-lib.jar|*-sources.jar|*-javadoc.jar) rm -f "$f" ;; \
        *) fat="$f" ;; \
      esac; \
    done; \
    if [ -z "$fat" ] || [ ! -f "$fat" ]; then \
      echo "error: Boot fat jar not found under ${work_dir}" >&2; \
      ls -la "${work_dir}" >&2; \
      exit 1; \
    fi; \
    mv -f "$fat" kernel-bio-converter.jar; \
    chown -R ${container_user}:${container_user} ${work_dir}

# Select container user for all tasks
USER ${container_user_uid}:${container_user_gid}

EXPOSE 8079

# JVM GC / --add-opens flags: use run-local.bat / run-local.sh for local IDE runs.
# Cluster may inject JDK_JAVA_OPTIONS via Helm. Keep this CMD short.
# Shell form so ${..._env} expands at container start.
CMD java -Dfile.encoding=UTF-8 \
    -Dloader.path="${loader_path_env}" \
    -Dspring.cloud.config.label="${spring_config_label_env}" \
    -Dspring.profiles.active="${active_profile_env}" \
    -Dspring.cloud.config.uri="${spring_config_url_env}" \
    -jar kernel-bio-converter.jar
