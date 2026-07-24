FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Önce bağımlılıkları indiriyoruz (Docker cache için)
COPY pom.xml .
RUN mvn dependency:go-offline

# Sonra kodu kopyalayıp build alıyoruz
COPY src src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=dev"]
