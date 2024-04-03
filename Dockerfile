FROM openjdk:17-alpine
EXPOSE 8080

COPY target/pms-1.0.1.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java","-jar","app.jar"]