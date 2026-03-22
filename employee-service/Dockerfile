FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Replace <modules> block with only the modules we need
RUN awk '/<modules>/{p=1; print "  <modules>"; print "    <module>common</module>"; print "    <module>employee-service</module>"; print "  </modules>"; next} p && /<\/modules>/{p=0; next} p{next} {print}' pom.xml > /tmp/pom.xml && mv /tmp/pom.xml pom.xml
COPY common ./common
COPY employee-service ./employee-service
RUN mvn clean package -pl common,employee-service -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/employee-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
