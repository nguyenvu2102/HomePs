FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM tomcat:10.1-jdk17-temurin
WORKDIR /usr/local/tomcat/webapps/

# Deploy WAR as ROOT so the app runs at http://localhost:8080/
COPY --from=build /app/target/HomePS.war ./ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
