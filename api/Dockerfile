FROM gradle:8.10-jdk17 AS builder

COPY --chown=gradle:gradle . /api
WORKDIR /api

RUN gradle clean build -x test --no-daemon

FROM gcr.io/distroless/java17-debian12

ARG JAR_FILE=/api/build/libs/*.jar
COPY --from=builder ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]