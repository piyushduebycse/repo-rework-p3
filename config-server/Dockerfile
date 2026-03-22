FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Replace <modules> block with only the modules we need
RUN awk '/<modules>/{p=1; print "  <modules>"; print "    <module>common</module>"; print "    <module>config-server</module>"; print "  </modules>"; next} p && /<\/modules>/{p=0; next} p{next} {print}' pom.xml > /tmp/pom.xml && mv /tmp/pom.xml pom.xml
COPY common ./common
COPY config-server ./config-server
RUN mvn clean package -pl config-server -am -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
