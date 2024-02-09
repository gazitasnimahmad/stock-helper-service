FROM openjdk:17-alpine
COPY target/*.jar ROOT.jar
ENTRYPOINT ["java","-jar","ROOT.jar"]
EXPOSE 8080