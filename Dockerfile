FROM eclipse-temurin:21-jre
LABEL maintainer="Mohammad Saadati mohammadsaadati2003@gmail.com"
COPY target/library-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
