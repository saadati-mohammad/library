FROM eclipse-temurin:21-jre
LABEL maintainer="Mohammad Saadati"
COPY target/library-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
