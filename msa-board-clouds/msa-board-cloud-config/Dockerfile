FROM openjdk:8

ENV TZ=Asia/Seoul
ENV LC_ALL=C.UTF-8

# Application Folder Create
RUN mkdir /app

# Excutable Jar Copy
COPY target/excutable-jar/msa-board-cloud-config-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT [ "java", "-jar", "/app/msa-board-cloud-config-0.0.1-SNAPSHOT.jar" ]