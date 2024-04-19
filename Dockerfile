FROM eclipse-temurin:21-ubi9-minimal
VOLUME /tmp
ARG JAR_FILE=build/libs/visma-sync-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x ./entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
