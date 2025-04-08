FROM openjdk:23-jdk-slim as builder

WORKDIR /app

COPY gradlew gradlew.bat settings.gradle build.gradle /app/
COPY gradle /app/gradle
RUN chmod +x ./gradlew

COPY src /app/src
RUN ./gradlew bootJar --no-daemon && \
    rm -rf /root/.gradle

FROM openjdk:23-jdk-slim

WORKDIR /app

COPY --from=builder /app/build/libs/sharding.storage-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]