FROM maven:3.9.9-eclipse-temurin-11 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 3003

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
