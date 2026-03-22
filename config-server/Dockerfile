FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Strip <modules> section so Maven doesn't look for sibling services
RUN sed -i 's/<modules>.*<\/modules>//g; /<modules>/,/<\/modules>/d' pom.xml
COPY common ./common
COPY config-server ./config-server
RUN mvn clean package -pl config-server -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
