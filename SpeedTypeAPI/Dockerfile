FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/speedtype-api-1.0.0.jar app.jar

EXPOSE 8080

ENV SERVER_PORT=8080 \
    DB_URL=jdbc:postgresql://db:5432/speedtype \
    DB_USERNAME=postgres \
    DB_PASSWORD=postgres \
    JWT_SECRET=secret

ENTRYPOINT ["java", "-jar", "app.jar"]

