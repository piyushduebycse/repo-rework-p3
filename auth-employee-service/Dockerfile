FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml ./pom.xml

# Copy common module
COPY common/pom.xml ./common/pom.xml
COPY common/src ./common/src

# Copy auth-employee-service
COPY auth-employee-service/pom.xml ./auth-employee-service/pom.xml
COPY auth-employee-service/src ./auth-employee-service/src

# Copy other module poms (for reactor resolution)
COPY leave-performance-service/pom.xml ./leave-performance-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml

# Build common first, then the service
RUN mvn -pl common -am install -DskipTests -q
RUN mvn -pl auth-employee-service -am package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/auth-employee-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
