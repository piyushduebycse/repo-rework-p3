FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Strip <modules> section so Maven doesn't look for sibling services
RUN sed -i 's/<modules>.*<\/modules>//g; /<modules>/,/<\/modules>/d' pom.xml
COPY common ./common
COPY employee-service ./employee-service
RUN mvn clean package -pl common,employee-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/employee-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
