FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests

RUN ls -lh target


FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /build/target/social-automation*.jar social-automation.jar

EXPOSE 4084

CMD ["java", "-jar", "social-automation.jar"]

