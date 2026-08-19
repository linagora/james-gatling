FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.17_8_1.9.1_2.13.11

WORKDIR /home/sbtuser/james-gatling

# Build context is the root of the project so that the image ships the code being built
COPY build.sbt ./
COPY project ./project
COPY lib ./lib
COPY src ./src
COPY dockerfiles/docker-runner/run.sh ./run.sh

RUN chmod 755 /home/sbtuser/james-gatling/run.sh && \
  chown -R sbtuser:sbtuser /home/sbtuser/james-gatling

USER sbtuser

RUN sbt clean compile

ENTRYPOINT [ "bash", "/home/sbtuser/james-gatling/run.sh" ]
