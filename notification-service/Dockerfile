FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml ./pom.xml

# Copy common module
COPY common/pom.xml ./common/pom.xml
COPY common/src ./common/src

# Copy notification-service
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY notification-service/src ./notification-service/src

# Copy other module poms (for reactor resolution)
COPY auth-employee-service/pom.xml ./auth-employee-service/pom.xml
COPY leave-performance-service/pom.xml ./leave-performance-service/pom.xml

# Build common first, then the service
RUN mvn -pl common -am install -DskipTests -q
RUN mvn -pl notification-service -am package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
