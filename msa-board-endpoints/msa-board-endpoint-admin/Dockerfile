FROM openjdk:8

ENV TZ=Asia/Seoul
ENV LC_ALL=C.UTF-8

# Application Folder Create
RUN mkdir -p /app/libs

# Common Module Jar Copy
COPY target/maven-jar/msa-board-common-0.0.1-SNAPSHOT.jar /app/libs

# Core Module Jar Copy
COPY target/maven-jar/msa-board-core-redis-0.0.1-SNAPSHOT.jar /app/libs

# Client Module Jar Copy
COPY target/maven-jar/msa-board-client-core-0.0.1-SNAPSHOT.jar /app/libs
COPY target/maven-jar/msa-board-client-member-0.0.1-SNAPSHOT.jar /app/libs
COPY target/maven-jar/msa-board-client-post-0.0.1-SNAPSHOT.jar /app/libs
COPY target/maven-jar/msa-board-client-search-0.0.1-SNAPSHOT.jar /app/libs

# Excutable Jar Copy
COPY target/excutable-jar/msa-board-endpoint-admin-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT [ \
	"java", "-cp", "/app/msa-board-endpoint-admin-0.0.1-SNAPSHOT.jar", \
	"-Dloader.path=/app/libs", "org.springframework.boot.loader.PropertiesLauncher"\
]