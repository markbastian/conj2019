FROM openjdk:8-alpine

COPY target/conj2019-0.1.0-SNAPSHOT-standalone.jar /bin/app.jar

EXPOSE 3000
EXPOSE 3001

CMD ["java", "-jar", "/bin/app.jar"]
