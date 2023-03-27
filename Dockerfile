ARG BUILD_ENV=develop

FROM maven:3.6.3-jdk-11 AS maven

## Build in developer's local machine, assume jar file already created.
FROM maven AS build-local
#COPY jmx_prometheus_javaagent-0.18.0.jar /sources/jmx_prometheus_javaagent-0.18.0.jar
COPY ./target/application.jar /build/application.jar

## Extract built jar file into layers
FROM build-${BUILD_ENV} AS extractor
WORKDIR /build/layers
RUN java -Djarmode=layertools -jar /build/application.jar extract

## Create final docker image.
FROM asia-southeast1-docker.pkg.dev/tk-dev-micro/base-image/distroless-java11
WORKDIR /app
COPY --from=extractor build/layers/dependencies/ ./
COPY --from=extractor build/layers/spring-boot-loader ./
COPY --from=extractor build/layers/snapshot-dependencies/ ./
COPY --from=extractor build/layers/project-modules/ ./
COPY --from=extractor build/layers/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]