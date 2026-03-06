FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 3009

ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "/app/app.jar"]
