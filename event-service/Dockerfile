FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /application
COPY . .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -pl event-service -am clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /application
COPY --from=builder /application/event-service/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]