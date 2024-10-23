FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY /target/planeStar-0.0.1-SNAPSHOT.jar planeStar-0.0.1.jar
ENTRYPOINT ["java","-jar","/planeStar-0.0.1.jar"]