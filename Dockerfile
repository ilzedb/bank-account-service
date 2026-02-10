# --- Build Stage ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a data directory for the H2 database file
RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir /data \
    && chown spring:spring /data


USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Map the data folder as a volume point
VOLUME /data

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]