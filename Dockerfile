FROM gradle:8.11.1-jdk21 AS builder

# 필요한 도구 설치 (dos2unix)
USER root
RUN apt-get update && apt-get install -y dos2unix

#Gradle 8.11.1 # 소스코드를 복사한 후, gradlew 파일의 CRLF를 LF로 변환
COPY . /usr/src
WORKDIR /usr/src
RUN dos2unix gradlew

# gradle wrapper 업데이트 및 빌드
RUN gradle wrapper --gradle-version 8.11.1
RUN ./gradlew clean build -x test

FROM openjdk:21-jdk
#debian기반
COPY --from=builder /usr/src/build/libs/spring-0.0.1-SNAPSHOT.jar /usr/app/app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/usr/app/app.jar"]