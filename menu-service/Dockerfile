FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/menu-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
