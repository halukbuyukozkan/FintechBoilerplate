FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradle/ ./gradle/
COPY gradlew ./
COPY gradlew.bat ./
COPY build.gradle ./
COPY settings.gradle ./

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

COPY AccountService/ ./AccountService/
COPY GlobalExceptionHandlerLib/ ./GlobalExceptionHandlerLib/

ENV JAVA_TOOL_OPTIONS="-Xmx1024m -XX:MaxMetaspaceSize=256m"
RUN ./gradlew :AccountService:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app

COPY --from=build /app/AccountService/build/libs/*.jar application.jar

EXPOSE 9591
ENTRYPOINT ["java","-jar","application.jar"]
