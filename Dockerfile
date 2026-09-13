FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY gradle ./gradle
COPY gradlew build.gradle.kts gradle.properties settings.gradle.kts ./
COPY src ./src

RUN ./gradlew installDist --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /opt/app

COPY --from=build /workspace/build/install/helmes-kotlin ./

EXPOSE 8080

ENTRYPOINT ["/opt/app/bin/helmes-kotlin"]
