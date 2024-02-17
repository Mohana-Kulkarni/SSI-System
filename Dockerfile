FROM maven:3.8.5-openjdk-17 as builder
RUN mkdir -p /app/source
COPY . /app/source
WORKDIR /app/source
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim as runtime
COPY --from=builder /app/source/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]