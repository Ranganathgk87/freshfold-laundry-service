FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/laundry-service-1.0.0.jar /app/laundry-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/laundry-service.jar"]
