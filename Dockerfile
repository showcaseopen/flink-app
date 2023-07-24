# Set base image
FROM flink:1.17.1-scala_2.12-java11

USER root:root
# Set the working directory in the Docker image
WORKDIR /opt/flink/usrlib

# Copy your Flink job JAR file to the working directory
COPY target/flink-streaming-job-*.jar /opt/flink/usrlib/myjob.jar

COPY flink-conf-local.yml /opt/flink/conf/flink-conf.yml

COPY src/main/resources/dataworkspace/ /dataworkspace/

COPY src/main/resources/config_local.properties /config.properties

COPY log4j2.properties /opt/flink/conf/

# Set the working directory back to /opt/flink
WORKDIR /opt/flink

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED:$JAVA_OPTS"


RUN touch /dataworkspace/output

RUN chmod a+w /dataworkspace/output