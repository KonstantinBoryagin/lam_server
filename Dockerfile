FROM nexus-docker2.sezinno.ru/maven:3.8.5-jdk-8-slim as builder
COPY maven_settings/settings.xml /root/.m2/
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
RUN mvn -B dependency:resolve dependency:resolve-plugins
COPY src /build/src
RUN mvn package
#с пропуском тестов
#RUN mvn package -DskipTests

FROM nexus-docker2.sezinno.ru/adoptopenjdk/openjdk8:ubi
EXPOSE 8080
ARG JAR_FILE=/build/target/*.jar
COPY --from=builder  ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
