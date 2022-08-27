FROM adoptopenjdk/openjdk11
MAINTAINER Minseok kim <halstjri@naver.com>

EXPOSE 8080

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew Bootjar

WORKDIR /build/libs

ENTRYPOINT ["java", "-jar", "jpashop-0.0.1-SNAPSHOT.jar"]