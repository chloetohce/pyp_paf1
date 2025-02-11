FROM eclipse-temurin:23-noble AS builder
LABEL name="bedandbbreakfast"
ARG COMPILEDIR=/compiledir

WORKDIR ${COMPILEDIR}

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN chmod a+x mvnw && ./mvnw package -Dmaven.test.skip=true


FROM eclipse-temurin:23-jre-noble
ARG WORKDIR=/app
WORKDIR ${WORKDIR}

COPY --from=builder /compiledir/target/assessment-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT SERVER_PORT=${SERVER_PORT} java -jar app.jar