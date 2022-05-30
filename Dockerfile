FROM eclipse-temurin:18-jdk AS builder
RUN mkdir /tmp/building
WORKDIR /tmp/building
COPY . ./
RUN ./gradlew clean build && ls /tmp/building/build/libs/

FROM eclipse-temurin:18-jdk
WORKDIR /root/
COPY --from=builder /tmp/building/build/libs/nodejs-vertx-1.0.0-SNAPSHOT-fat.jar ./
CMD ["java", "-jar", "nodejs-vertx-1.0.0-SNAPSHOT-fat.jar"]