FROM mosipid/openjdk-21-jre:21.0.4

ARG SOURCE
ARG COMMIT_HASH
ARG COMMIT_ID
ARG BUILD_TIME
LABEL source=${SOURCE} \
      commit_hash=${COMMIT_HASH} \
      commit_id=${COMMIT_ID} \
      build_time=${BUILD_TIME}

ARG spring_config_label=master
ARG active_profile=default
ARG spring_config_url
ARG container_user=mosip
ARG container_user_group=mosip
ARG container_user_uid=1001
ARG container_user_gid=1001

ENV active_profile_env=${active_profile} \
    spring_config_label_env=${spring_config_label} \
    spring_config_url_env=${spring_config_url} \
    work_dir=/home/${container_user} \
    loader_path_env=/home/${container_user}/additional_jars/

WORKDIR ${work_dir}

RUN apt-get update && \
    apt-get install -y --no-install-recommends sudo && \
    groupadd -g ${container_user_gid} ${container_user_group} && \
    useradd -u ${container_user_uid} -g ${container_user_group} -s /bin/bash -m ${container_user} && \
    mkdir -p ${work_dir}/additional_jars && \
    rm -rf /var/lib/apt/lists/*

COPY ./target/kernel-bio-converter-*.jar ${work_dir}/kernel-bio-converter.jar
# Prefer fat JAR only (exclude -lib / -sources / -javadoc if COPY matched multiple)
RUN set -eux; \
    cd ${work_dir}; \
    for f in kernel-bio-converter-*.jar; do \
      case "$f" in \
        *-lib.jar|*-sources.jar|*-javadoc.jar) rm -f "$f" ;; \
        *) mv -f "$f" kernel-bio-converter.jar ;; \
      esac; \
    done; \
    chown -R ${container_user}:${container_user} ${work_dir}

USER ${container_user_uid}:${container_user_gid}
EXPOSE 8079

# JVM GC / --add-opens flags: use run-local.bat / run-local.sh for local IDE runs.
# Shell form so ${..._env} expands at container start.
CMD java -Dfile.encoding=UTF-8 \
    -Dloader.path="${loader_path_env}" \
    -Dspring.cloud.config.label="${spring_config_label_env}" \
    -Dspring.profiles.active="${active_profile_env}" \
    -Dspring.cloud.config.uri="${spring_config_url_env}" \
    -jar kernel-bio-converter.jar
