# We use Java 23 Runtime for running the Quarkus application
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the built artifacts from your local 'target' folder into the container
# Quarkus creates a 'quarkus-app' folder when you run 'mvn package'
COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/*.jar /app/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8080

# Run the application
CMD ["java", "-jar", "quarkus-run.jar"]