FROM openjdk:17-jdk-alpine

RUN apk add --no-cache ffmpeg

VOLUME /tmp
VOLUME /downloads/transcoded
ARG JAR_FILE=target/video-transcoder-service-image-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
