FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM tomcat:10.1-jdk17-temurin
WORKDIR /usr/local/tomcat/webapps/

# Đổi tên file war thành ROOT.war để chạy ở trang chủ localhost:8080/ thay vì localhost:8080/HomePS/
COPY --from=build /app/target/HomePS.war ./ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]