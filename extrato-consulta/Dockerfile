FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml pom.xml
COPY shared-contracts/pom.xml shared-contracts/pom.xml
COPY extrato-ingestor/pom.xml extrato-ingestor/pom.xml
COPY extrato-gravador/pom.xml extrato-gravador/pom.xml
COPY extrato-consulta/pom.xml extrato-consulta/pom.xml
COPY shared-contracts/src shared-contracts/src
COPY extrato-ingestor/src extrato-ingestor/src
COPY extrato-gravador/src extrato-gravador/src
COPY extrato-consulta/src extrato-consulta/src

RUN mvn -Pplano-b-jvm -pl extrato-consulta -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/extrato-consulta/target/extrato-consulta-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
